package com.example.dao;

import com.example.model.Team;
import com.example.model.User;
import com.example.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TeamDAO {
    
    // 添加团队
    public boolean addTeam(Team team) {
        String sql = "INSERT INTO teams (team_name, leader_id, established_date, research_area, description) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, team.getTeamName());
            pstmt.setInt(2, team.getLeaderId());
            pstmt.setDate(3, team.getEstablishedDate() != null ? new java.sql.Date(team.getEstablishedDate().getTime()) : null);
            pstmt.setString(4, team.getResearchArea());
            pstmt.setString(5, team.getDescription());
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    team.setId(rs.getInt(1));
                }
                return true;
            }
            return false;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // 获取所有团队
    public List<Team> getAllTeams() {
        List<Team> teams = new ArrayList<>();
        String sql = "SELECT t.*, u.real_name as leader_name FROM teams t " +
                     "LEFT JOIN users u ON t.leader_id = u.id " +
                     "ORDER BY t.established_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Team team = extractTeamFromResultSet(rs);
                teams.add(team);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return teams;
    }
    
    // 根据ID获取团队
    public Team getTeamById(int id) {
        String sql = "SELECT t.*, u.real_name as leader_name FROM teams t " +
                     "LEFT JOIN users u ON t.leader_id = u.id " +
                     "WHERE t.id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractTeamFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // 获取负责人为指定用户的团队
    public List<Team> getTeamsByLeader(int leaderId) {
        List<Team> teams = new ArrayList<>();
        String sql = "SELECT t.*, u.real_name as leader_name FROM teams t " +
                     "LEFT JOIN users u ON t.leader_id = u.id " +
                     "WHERE t.leader_id = ? ORDER BY t.established_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, leaderId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Team team = extractTeamFromResultSet(rs);
                teams.add(team);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return teams;
    }
    
    // 获取用户所属的团队
    public List<Team> getTeamsByMember(int userId) {
        List<Team> teams = new ArrayList<>();
        String sql = "SELECT t.*, u.real_name as leader_name FROM teams t " +
                     "LEFT JOIN users u ON t.leader_id = u.id " +
                     "INNER JOIN team_members tm ON t.id = tm.team_id " +
                     "WHERE tm.user_id = ? ORDER BY t.established_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Team team = extractTeamFromResultSet(rs);
                teams.add(team);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return teams;
    }
    
    // 更新团队
    public boolean updateTeam(Team team) {
        String sql = "UPDATE teams SET team_name = ?, leader_id = ?, established_date = ?, research_area = ?, description = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, team.getTeamName());
            pstmt.setInt(2, team.getLeaderId());
            pstmt.setDate(3, team.getEstablishedDate() != null ? new java.sql.Date(team.getEstablishedDate().getTime()) : null);
            pstmt.setString(4, team.getResearchArea());
            pstmt.setString(5, team.getDescription());
            pstmt.setInt(6, team.getId());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // 删除团队
    public boolean deleteTeam(int teamId) {
        // 先删除团队成员关联
        String deleteMembersSql = "DELETE FROM team_members WHERE team_id = ?";
        String deleteTeamSql = "DELETE FROM teams WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            
            try {
                // 删除团队成员
                try (PreparedStatement pstmt1 = conn.prepareStatement(deleteMembersSql)) {
                    pstmt1.setInt(1, teamId);
                    pstmt1.executeUpdate();
                }
                
                // 删除团队
                try (PreparedStatement pstmt2 = conn.prepareStatement(deleteTeamSql)) {
                    pstmt2.setInt(1, teamId);
                    int rowsAffected = pstmt2.executeUpdate();
                    
                    conn.commit();
                    return rowsAffected > 0;
                }
                
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // 添加团队成员
    public boolean addTeamMember(int teamId, int userId, String position) {
        String sql = "INSERT INTO team_members (team_id, user_id, join_date, position) VALUES (?, ?, CURDATE(), ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, teamId);
            pstmt.setInt(2, userId);
            pstmt.setString(3, position);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // 移除团队成员
    public boolean removeTeamMember(int teamId, int userId) {
        String sql = "DELETE FROM team_members WHERE team_id = ? AND user_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, teamId);
            pstmt.setInt(2, userId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // 获取团队成员
    public List<User> getTeamMembers(int teamId) {
        List<User> members = new ArrayList<>();
        String sql = "SELECT u.*, tm.position FROM users u " +
                     "INNER JOIN team_members tm ON u.id = tm.user_id " +
                     "WHERE tm.team_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, teamId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setRealName(rs.getString("real_name"));
                user.setEmail(rs.getString("email"));
                user.setRole(rs.getString("role"));
                user.setDepartment(rs.getString("department"));
                user.setTitle(rs.getString("title"));
                members.add(user);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return members;
    }
    
    // 检查用户是否是团队成员
    public boolean isTeamMember(int teamId, int userId) {
        String sql = "SELECT id FROM team_members WHERE team_id = ? AND user_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, teamId);
            pstmt.setInt(2, userId);
            ResultSet rs = pstmt.executeQuery();
            
            return rs.next();
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // 从ResultSet提取Team对象
    private Team extractTeamFromResultSet(ResultSet rs) throws SQLException {
        Team team = new Team();
        team.setId(rs.getInt("id"));
        team.setTeamName(rs.getString("team_name"));
        team.setLeaderId(rs.getInt("leader_id"));
        team.setLeaderName(rs.getString("leader_name"));
        team.setEstablishedDate(rs.getDate("established_date"));
        team.setResearchArea(rs.getString("research_area"));
        team.setDescription(rs.getString("description"));
        team.setCreatedAt(rs.getTimestamp("created_at"));
        return team;
    }
}