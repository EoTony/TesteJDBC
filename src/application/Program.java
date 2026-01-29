package application;

import model.dao.DaoFactory;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

import java.util.List;

public class Program {
    public static void main(String[] args) {

        SellerDao sellerDao = DaoFactory.createSellerDao();

        System.out.println("===Teste findById===");
        Seller seller = sellerDao.findById(1);
        System.out.println(seller);
        System.out.println();

        System.out.println("===Teste findByDepartment===");
        Department department = new Department("null",1);
        List<Seller> sellers = sellerDao.findByDepartment(department);
        sellers.forEach(System.out::println);
        System.out.println();

        System.out.println("Teste findAll===");
        List<Seller> sellersAll = sellerDao.findAll();
        sellersAll.forEach(System.out::println);
        System.out.println();


    }
}
