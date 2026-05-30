package com.example.dao;

import com.example.model.Project;
import com.example.model.User;
import com.example.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProjectDAO {
    
    // 添加项目
    public boolean addProject(Project project) {
        String sql = "INSERT INTO projects (project_name, project_number, leader_id, budget, start_date, end_date, status, description) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, project.getProjectName());
            pstmt.setString(2, project.getProjectNumber());
            pstmt.setInt(3, project.getLeaderId());
            pstmt.setDouble(4, project.getBudget());
            pstmt.setDate(5, new java.sql.Date(project.getStartDate().getTime()));
            pstmt.setDate(6, new java.sql.Date(project.getEndDate().getTime()));
            pstmt.setString(7, project.getStatus());
            pstmt.setString(8, project.getDescription());
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    project.setId(rs.getInt(1));
                }
                return true;
            }
            return false;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // 获取所有项目
    public List<Project> getAllProjects() {
        List<Project> projects = new ArrayList<>();
        String sql = "SELECT p.*, u.real_name as leader_name FROM projects p " +
                     "LEFT JOIN users u ON p.leader_id = u.id " +
                     "ORDER BY p.start_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Project project = extractProjectFromResultSet(rs);
                projects.add(project);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return projects;
    }
    
    // 根据ID获取项目
    public Project getProjectById(int id) {
        String sql = "SELECT p.*, u.real_name as leader_name FROM projects p " +
                     "LEFT JOIN users u ON p.leader_id = u.id " +
                     "WHERE p.id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractProjectFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // 获取负责人为指定用户的项目
    public List<Project> getProjectsByLeader(int leaderId) {
        List<Project> projects = new ArrayList<>();
        String sql = "SELECT p.*, u.real_name as leader_name FROM projects p " +
                     "LEFT JOIN users u ON p.leader_id = u.id " +
                     "WHERE p.leader_id = ? ORDER BY p.start_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, leaderId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Project project = extractProjectFromResultSet(rs);
                projects.add(project);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return projects;
    }
    
    // 获取用户参与的项目（通过项目成员表）
    public List<Project> getProjectsByMember(int userId) {
        List<Project> projects = new ArrayList<>();
        String sql = "SELECT p.*, u.real_name as leader_name FROM projects p " +
                     "LEFT JOIN users u ON p.leader_id = u.id " +
                     "INNER JOIN project_members pm ON p.id = pm.project_id " +
                     "WHERE pm.user_id = ? ORDER BY p.start_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Project project = extractProjectFromResultSet(rs);
                projects.add(project);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return projects;
    }
    
    // 更新项目
    public boolean updateProject(Project project) {
        String sql = "UPDATE projects SET project_name = ?, project_number = ?, leader_id = ?, " +
                     "budget = ?, start_date = ?, end_date = ?, status = ?, description = ? " +
                     "WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, project.getProjectName());
            pstmt.setString(2, project.getProjectNumber());
            pstmt.setInt(3, project.getLeaderId());
            pstmt.setDouble(4, project.getBudget());
            pstmt.setDate(5, new java.sql.Date(project.getStartDate().getTime()));
            pstmt.setDate(6, new java.sql.Date(project.getEndDate().getTime()));
            pstmt.setString(7, project.getStatus());
            pstmt.setString(8, project.getDescription());
            pstmt.setInt(9, project.getId());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // 更新项目状态
    public boolean updateProjectStatus(int projectId, String status) {
        String sql = "UPDATE projects SET status = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            pstmt.setInt(2, projectId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // 删除项目
    public boolean deleteProject(int projectId) {
        // 先删除项目成员关联
        String deleteMembersSql = "DELETE FROM project_members WHERE project_id = ?";
        String deleteProjectSql = "DELETE FROM projects WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            
            try {
                // 删除项目成员
                try (PreparedStatement pstmt1 = conn.prepareStatement(deleteMembersSql)) {
                    pstmt1.setInt(1, projectId);
                    pstmt1.executeUpdate();
                }
                
                // 删除项目
                try (PreparedStatement pstmt2 = conn.prepareStatement(deleteProjectSql)) {
                    pstmt2.setInt(1, projectId);
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
    
    // 添加项目成员
    public boolean addProjectMember(int projectId, int userId, String role) {
        String sql = "INSERT INTO project_members (project_id, user_id, role_in_project, join_date) VALUES (?, ?, ?, CURDATE())";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, projectId);
            pstmt.setInt(2, userId);
            pstmt.setString(3, role);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // 获取项目成员
    public List<User> getProjectMembers(int projectId) {
        List<User> members = new ArrayList<>();
        String sql = "SELECT u.*, pm.role_in_project FROM users u " +
                     "INNER JOIN project_members pm ON u.id = pm.user_id " +
                     "WHERE pm.project_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, projectId);
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
    
    // 从ResultSet提取Project对象
    private Project extractProjectFromResultSet(ResultSet rs) throws SQLException {
        Project project = new Project();
        project.setId(rs.getInt("id"));
        project.setProjectName(rs.getString("project_name"));
        project.setProjectNumber(rs.getString("project_number"));
        project.setLeaderId(rs.getInt("leader_id"));
        project.setLeaderName(rs.getString("leader_name"));
        project.setBudget(rs.getDouble("budget"));
        project.setStartDate(rs.getDate("start_date"));
        project.setEndDate(rs.getDate("end_date"));
        project.setStatus(rs.getString("status"));
        project.setDescription(rs.getString("description"));
        project.setCreatedAt(rs.getTimestamp("created_at"));
        return project;
    }
}