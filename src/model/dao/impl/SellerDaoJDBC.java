package model.dao.impl;

import db.Db;
import db.DbException;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public class SellerDaoJDBC  implements SellerDao {

    private Connection connection;

    public SellerDaoJDBC(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void insert(Seller seller) {
        PreparedStatement st = null;
        try{
            st = connection.prepareStatement(
                    "INSERT INTO seller "+
                            "(Name, Email, BirthDate, BaseSalary, DepartmentId) "+
                    "VALUES "+
                            "(?, ?, ?, ?, ?) ",
                    Statement.RETURN_GENERATED_KEYS
            );

            st.setString(1,seller.getName());
            st.setString(2,seller.getEmail());
            st.setObject(3,seller.getBirthDate());
            st.setDouble(4,seller.getBaseSalary());
            st.setInt(5,seller.getDepartment().getId());

            int linhas = st.executeUpdate();
            if (linhas > 0) {
                ResultSet rs = st.getGeneratedKeys();
                if (rs.next()) {
                    int id = rs.getInt(1);
                    seller.setId(id);
                }
                Db.closeResultSet(rs);
            }
            else{
                throw new DbException("Erro ao inserir seller");
            }

        }
        catch(SQLException e){
            throw new DbException(e.getMessage());
        }
        finally{
            Db.closeStatement(st);
        }
    }

    @Override
    public void update(Seller seller) {
        PreparedStatement st = null;

        try{
             st = connection.prepareStatement( "UPDATE seller "+
            "SET Name = ?, Email = ?, BirthDate = ?, BaseSalary = ?, DepartmentId = ? "+
                    "WHERE Id = ? "
            );

            st.setString(1,seller.getName());
            st.setString(2,seller.getEmail());
            st.setObject(3,seller.getBirthDate());
            st.setDouble(4,seller.getBaseSalary());
            st.setInt(5,seller.getDepartment().getId());
            st.setInt(6,seller.getId());

            st.executeUpdate();

        }
        catch(SQLException e){
            throw new DbException(e.getMessage());
        }
        finally{
            Db.closeStatement(st);
        }
    }

    @Override
    public void deleteById(Integer id) {
        PreparedStatement st = null;
        try{
            st = connection.prepareStatement(
                    "DELETE FROM seller " +
                            "WHERE Id = ? "
            );
            st.setInt(1,id);
            int  linhas = st.executeUpdate();
            if (linhas < 1) {
                throw new InputMismatchException("Erro ao deletar seller. Id invalido!");
            }


        }
        catch(SQLException e) {
            throw new DbException(e.getMessage());
        }
        finally{
            Db.closeStatement(st);
        }
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
        seller.setBirthDate(rs.getObject("BirthDate",LocalDate.class));
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
