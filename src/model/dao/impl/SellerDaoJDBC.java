package model.dao.impl;

import db.Db;
import db.DbException;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SellerDaoJDBC  implements SellerDao {

    private Connection connection;

    public SellerDaoJDBC(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void insert(Seller seller) {

    }

    @Override
    public void update(Seller seller) {

    }

    @Override
    public void delete(Seller seller) {

    }

    @Override
    public Seller findById(Integer id) {
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = connection.prepareStatement(
                    "SELECT seller.*,department.Name as DepName " +
                            "FROM seller INNER JOIN department " +
                            "ON seller.DepartmentId = department.Id " +
                            "WHERE seller.Id = ? "
            );
            st.setInt(1, id);
            rs = st.executeQuery();

            if (rs.next()) {
                Department dep = instatiationDepartment(rs);
                Seller seller = instatiationSeller(rs, dep);
                return seller;
            }
            return null;

        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            Db.closeStatement(st);
            Db.closeResultSet(rs);
        }
    }

    @Override
    public List<Seller> findByDepartment(Department department) {
        PreparedStatement st = null;
        ResultSet rs = null;
        List<Seller> sellers = new ArrayList<>();
        Map<Integer, Department> map = new HashMap<>();

        try {
            st = connection.prepareStatement(
                    "SELECT seller.*,department.Name as DepName " +
                            "FROM seller INNER JOIN department " +
                            "ON seller.DepartmentId = department.Id " +
                            "WHERE DepartmentId = ? " +
                            "ORDER BY Name"
            );
            st.setInt(1, department.getId());
            rs = st.executeQuery();
            while (rs.next()) {
                Department dep = map.get(rs.getInt("DepartmentId"));
                if (dep == null) {
                    dep = instatiationDepartment(rs);
                    map.put(rs.getInt("DepartmentId"), dep);
                }
                Seller seller = instatiationSeller(rs, dep);
                sellers.add(seller);
            }
            return sellers;
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            Db.closeStatement(st);
            Db.closeResultSet(rs);
        }
    }


    @Override
    public List<Seller> findAll() {
        PreparedStatement st = null;
        ResultSet rs = null;
        List<Seller> sellers = new ArrayList<>();
        Map<Integer, Department> map = new HashMap<>();

        try {
            st = connection.prepareStatement(
                    "SELECT seller.*,department.Name as DepName " +
                            "FROM seller INNER JOIN department " +
                            "ON seller.DepartmentId = department.Id " +
                            "ORDER BY Name"
            );

            rs = st.executeQuery();
            while (rs.next()) {
                Department dep = map.get(rs.getInt("DepartmentId"));
                if (dep == null) {
                    dep = instatiationDepartment(rs);
                    map.put(rs.getInt("DepartmentId"), dep);
                }
                Seller seller = instatiationSeller(rs, dep);
                sellers.add(seller);
            }
            return sellers;
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            Db.closeStatement(st);
            Db.closeResultSet(rs);
        }
    }

    private Seller instatiationSeller(ResultSet rs,Department dep) throws SQLException {
        Seller seller = new Seller();
        seller.setId(rs.getInt("id"));
        seller.setName(rs.getString("Name"));
        seller.setEmail(rs.getString("Email"));
        seller.setBirthDate(rs.getObject("BirthDate",LocalDateTime.class));
        seller.setBaseSalary(rs.getDouble("BaseSalary"));
        seller.setDepartment(dep);

        return seller;
    }

    private Department instatiationDepartment(ResultSet rs) throws SQLException {
        Department dep =  new Department();
        dep.setId(rs.getInt("DepartmentId"));
        dep.setName(rs.getString("DepName"));

        return dep;
    }
}
