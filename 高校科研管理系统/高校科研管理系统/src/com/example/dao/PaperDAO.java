package com.example.dao;

import com.example.model.Paper;
import com.example.model.User;
import com.example.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PaperDAO {
    
    // 添加论文
    public boolean addPaper(Paper paper) {
        String sql = "INSERT INTO papers (title, author_id, co_authors, journal, publish_date, volume, issue, pages, doi, level, project_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, paper.getTitle());
            pstmt.setInt(2, paper.getAuthorId());
            pstmt.setString(3, paper.getCoAuthors());
            pstmt.setString(4, paper.getJournal());
            pstmt.setDate(5, paper.getPublishDate() != null ? new java.sql.Date(paper.getPublishDate().getTime()) : null);
            pstmt.setString(6, paper.getVolume());
            pstmt.setString(7, paper.getIssue());
            pstmt.setString(8, paper.getPages());
            pstmt.setString(9, paper.getDoi());
            pstmt.setString(10, paper.getLevel());
            pstmt.setObject(11, paper.getProjectId() > 0 ? paper.getProjectId() : null);
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    paper.setId(rs.getInt(1));
                }
                return true;
            }
            return false;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // 获取所有论文
    public List<Paper> getAllPapers() {
        List<Paper> papers = new ArrayList<>();
        String sql = "SELECT p.*, u.real_name as author_name, pr.project_name " +
                     "FROM papers p " +
                     "LEFT JOIN users u ON p.author_id = u.id " +
                     "LEFT JOIN projects pr ON p.project_id = pr.id " +
                     "ORDER BY p.publish_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Paper paper = extractPaperFromResultSet(rs);
                papers.add(paper);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return papers;
    }
    
    // 根据ID获取论文
    public Paper getPaperById(int id) {
        String sql = "SELECT p.*, u.real_name as author_name, pr.project_name " +
                     "FROM papers p " +
                     "LEFT JOIN users u ON p.author_id = u.id " +
                     "LEFT JOIN projects pr ON p.project_id = pr.id " +
                     "WHERE p.id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractPaperFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // 获取作者的论文
    public List<Paper> getPapersByAuthor(int authorId) {
        List<Paper> papers = new ArrayList<>();
        String sql = "SELECT p.*, u.real_name as author_name, pr.project_name " +
                     "FROM papers p " +
                     "LEFT JOIN users u ON p.author_id = u.id " +
                     "LEFT JOIN projects pr ON p.project_id = pr.id " +
                     "WHERE p.author_id = ? " +
                     "ORDER BY p.publish_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, authorId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Paper paper = extractPaperFromResultSet(rs);
                papers.add(paper);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return papers;
    }
    
    // 获取项目的论文
    public List<Paper> getPapersByProject(int projectId) {
        List<Paper> papers = new ArrayList<>();
        String sql = "SELECT p.*, u.real_name as author_name, pr.project_name " +
                     "FROM papers p " +
                     "LEFT JOIN users u ON p.author_id = u.id " +
                     "LEFT JOIN projects pr ON p.project_id = pr.id " +
                     "WHERE p.project_id = ? " +
                     "ORDER BY p.publish_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, projectId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Paper paper = extractPaperFromResultSet(rs);
                papers.add(paper);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return papers;
    }
    
    // 按级别获取论文
    public List<Paper> getPapersByLevel(String level) {
        List<Paper> papers = new ArrayList<>();
        String sql = "SELECT p.*, u.real_name as author_name, pr.project_name " +
                     "FROM papers p " +
                     "LEFT JOIN users u ON p.author_id = u.id " +
                     "LEFT JOIN projects pr ON p.project_id = pr.id " +
                     "WHERE p.level = ? " +
                     "ORDER BY p.publish_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, level);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Paper paper = extractPaperFromResultSet(rs);
                papers.add(paper);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return papers;
    }
    
    // 更新论文
    public boolean updatePaper(Paper paper) {
        String sql = "UPDATE papers SET title = ?, co_authors = ?, journal = ?, " +
                     "publish_date = ?, volume = ?, issue = ?, pages = ?, doi = ?, " +
                     "level = ?, project_id = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, paper.getTitle());
            pstmt.setString(2, paper.getCoAuthors());
            pstmt.setString(3, paper.getJournal());
            pstmt.setDate(4, paper.getPublishDate() != null ? new java.sql.Date(paper.getPublishDate().getTime()) : null);
            pstmt.setString(5, paper.getVolume());
            pstmt.setString(6, paper.getIssue());
            pstmt.setString(7, paper.getPages());
            pstmt.setString(8, paper.getDoi());
            pstmt.setString(9, paper.getLevel());
            pstmt.setObject(10, paper.getProjectId() > 0 ? paper.getProjectId() : null);
            pstmt.setInt(11, paper.getId());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // 删除论文
    public boolean deletePaper(int paperId) {
        String sql = "DELETE FROM papers WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, paperId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // 统计论文数量（按级别）
    public int countPapersByLevel(String level) {
        String sql = "SELECT COUNT(*) as count FROM papers WHERE level = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, level);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("count");
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    // 从ResultSet提取Paper对象
    private Paper extractPaperFromResultSet(ResultSet rs) throws SQLException {
        Paper paper = new Paper();
        paper.setId(rs.getInt("id"));
        paper.setTitle(rs.getString("title"));
        paper.setAuthorId(rs.getInt("author_id"));
        paper.setAuthorName(rs.getString("author_name"));
        paper.setCoAuthors(rs.getString("co_authors"));
        paper.setJournal(rs.getString("journal"));
        paper.setPublishDate(rs.getDate("publish_date"));
        paper.setVolume(rs.getString("volume"));
        paper.setIssue(rs.getString("issue"));
        paper.setPages(rs.getString("pages"));
        paper.setDoi(rs.getString("doi"));
        paper.setLevel(rs.getString("level"));
        paper.setProjectId(rs.getInt("project_id"));
        paper.setProjectName(rs.getString("project_name"));
        paper.setCreatedAt(rs.getTimestamp("created_at"));
        return paper;
    }
}