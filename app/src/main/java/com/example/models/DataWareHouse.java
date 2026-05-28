package com.example.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DataWareHouse implements Serializable {
    public static ArrayList<Category>getCategories()
    {
        ArrayList<Category> categories = new ArrayList<>();
        Category c1=new Category("c1","Trái Cây","Trái cây theo mùa");
        Category c2=new Category("c2","Kim chi","kim chi hàn quốc");
        Category c3=new Category("c3","Mì","Mì các loại");
        Category c4=new Category("c4","Thịt","thịt các loại");
        categories.add(c1);
        categories.add(c2);
        categories.add(c3);
        categories.add(c4);
        return categories;
    }
    public static ArrayList<Product>getProducts()
    {
        ArrayList<Product> products = new ArrayList<>();
        ArrayList<Category> categories = getCategories();

        // Trái Cây (c1)
        Product p1=new Product("p1","Táo Mỹ Envy",10,20000,0,0.05,categories.get(0).getCateId());
        Product p2=new Product("p2","Nho Mỹ",8,45000,0,0.05,categories.get(0).getCateId());
        Product p3=new Product("p3","Cam Sành",12,25000,0,0.05,categories.get(0).getCateId());

        // Kim chi (c2)
        Product p4=new Product("p4","Kim chi cải thảo",5,50000,0,0.05,categories.get(1).getCateId());
        Product p5=new Product("p5","Kim chi củ cải",6,40000,0,0.05,categories.get(1).getCateId());
        Product p6=new Product("p6","Kim chi dưa leo",4,35000,0,0.05,categories.get(1).getCateId());

        // Mì (c3)
        Product p7=new Product("p7","Mì cay Hàn Quốc",20,10000,0,0.05,categories.get(2).getCateId());
        Product p8=new Product("p8","Mì trộn",15,12000,0,0.05,categories.get(2).getCateId());
        Product p9=new Product("p9","Mì gói tôm chua",18,9000,0,0.05,categories.get(2).getCateId());

        // Thịt (c4)
        Product p10=new Product("p10","Thịt bò(300g)",2,150000,0,0.05,categories.get(3).getCateId());
        Product p11=new Product("p11","Thịt heo(500g)",3,120000,0,0.05,categories.get(3).getCateId());
        Product p12=new Product("p12","Ức gà(500g)",4,85000,0,0.05,categories.get(3).getCateId());

        products.add(p1);
        products.add(p2);
        products.add(p3);
        products.add(p4);
        products.add(p5);
        products.add(p6);
        products.add(p7);
        products.add(p8);
        products.add(p9);
        products.add(p10);
        products.add(p11);
        products.add(p12);
        return products;
    }

    public static ArrayList<Employee>getEmployee()
    {
        ArrayList<Employee> employees = new ArrayList<>();
        employees.add(new Employee("e1","Nguyễn Văn A","0123456789","Hà Nội"));
        employees.add(new Employee("e2","Trần Thị B","0987654321","Đà Nẵng"));
        employees.add(new Employee("e3","Lê Văn C","0912345678","Hải Phòng"));
        employees.add(new Employee("e4","Phạm Thị D","0901122334","TP.HCM"));
        employees.add(new Employee("e5","Hoàng Văn E","0933445566","Cần Thơ"));
        employees.add(new Employee("e6","Vũ Thị F","0977889900","Bình Dương"));
        employees.add(new Employee("e7","Đỗ Văn G","0966554433","Nghệ An"));
        employees.add(new Employee("e8","Ngô Thị H","0944221100","Khánh Hòa"));
        employees.add(new Employee("e9","Bùi Văn I","0922334455","Quảng Ninh"));
        employees.add(new Employee("e10","Đặng Thị K","0955667788","Huế"));
        return employees;
    }
    public static ArrayList<Customer>getCustomers(){
        ArrayList<Customer>customers=new ArrayList<>();
        customers.add(new Customer("cu1","Phạm Văn A","0123456789","a@gmail.com","Hà Nội",new GregorianCalendar(1962, 4, 12).getTime()));
        customers.add(new Customer("cu2","Nguyễn Thị B","0987654321","b@gmail.com","Đà Nẵng",new GregorianCalendar(1970, 9, 5).getTime()));
        customers.add(new Customer("cu3","Lê Văn C","0912345678","c@gmail.com","Hải Phòng",new GregorianCalendar(1978, 1, 20).getTime()));
        customers.add(new Customer("cu4","Trần Thị D","0901122334","d@gmail.com","TP.HCM",new GregorianCalendar(1985, 6, 30).getTime()));
        customers.add(new Customer("cu5","Phạm Văn E","0933445566","e@gmail.com","Cần Thơ",new GregorianCalendar(1990, 10, 15).getTime()));
        customers.add(new Customer("cu6","Vũ Thị F","0977889900","f@gmail.com","Bình Dương",new GregorianCalendar(1995, 2, 8).getTime()));
        customers.add(new Customer("cu7","Đỗ Văn G","0966554433","g@gmail.com","Nghệ An",new GregorianCalendar(1999, 7, 27).getTime()));
        customers.add(new Customer("cu8","Ngô Thị H","0944221100","h@gmail.com","Khánh Hòa",new GregorianCalendar(2003, 11, 3).getTime()));
        customers.add(new Customer("cu9","Bùi Văn I","0922334455","i@gmail.com","Quảng Ninh",new GregorianCalendar(2007, 0, 18).getTime()));
        customers.add(new Customer("cu10","Đặng Thị K","0955667788","k@gmail.com","Huế",new GregorianCalendar(2010, 3, 9).getTime()));
        return customers;
    }
    public static ArrayList<Order>getOrders()
    {
        ArrayList<Order> orders = new ArrayList<>();
        ArrayList<Employee> employees = getEmployee();
        ArrayList<Customer> customers = getCustomers();

        Calendar calendar = Calendar.getInstance();
        int[] months2024 = {0,1,2,3,4,5,6,7,8,9,10,11};
        int[] months2025 = {0,1,2,3,4,5,6,7,8,9,10,11};
        int[] months2026 = {0,1,2};

        for (int i = 0; i < 100; i++) {
            int year;
            int month;
            if (i < 40) {
                year = 2024;
                month = months2024[i % months2024.length];
            } else if (i < 80) {
                year = 2025;
                month = months2025[i % months2025.length];
            } else {
                year = 2026;
                month = months2026[i % months2026.length];
            }

            int day = (i % 28) + 1;
            int hour = (i * 3) % 24;
            int minute = (i * 7) % 60;
            int second = (i * 11) % 60;

            calendar.set(year, month, day, hour, minute, second);
            String orderId = "o" + (i + 1);
            String customerId = customers.get(i % customers.size()).getCustomerId();
            String employeeId = employees.get(i % employees.size()).getId();
            orders.add(new Order(orderId, customerId, employeeId, calendar.getTime()));
        }
        return orders;
    }
    public static ArrayList<OrderDetail> getOrderDetails(ArrayList<Order> orders, ArrayList<Product> products)
    {
        ArrayList<OrderDetail> orderDetails=new ArrayList<>();

        int detailIndex = 1;
        for (int i = 0; i < orders.size(); i++) {
            Order order = orders.get(i);
            int detailCount = (i % 10) + 1;

            for (int j = 0; j < detailCount; j++) {
                Product product = products.get((i + j) % products.size());
                int maxQuantity = Math.max(1, product.getQuantity());
                int quantity = ((i + j) % maxQuantity) + 1;

                orderDetails.add(new OrderDetail(
                        "od" + detailIndex,
                        order.getOrderId(),
                        product.getProductId(),
                        quantity,
                        product.getPrices(),
                        product.getCoupon(),
                        product.getVAT()
                ));
                detailIndex++;
            }
        }
        return orderDetails;

    }

    public static double sumOfMoney(Order or)
    {
        double sum = 0;
        if (or == null) {
            return sum;
        }

        ArrayList<OrderDetail> orderDetails = getOrderDetails(getOrders(), getProducts());
        for (OrderDetail detail : orderDetails) {
            if (or.getOrderId().equals(detail.getOrderId())) {
                double lineTotal = detail.getQuantity() * detail.getPrice();
                lineTotal = lineTotal * (1 - detail.getCoupon());
                lineTotal = lineTotal * (1 + detail.getVAT());
                sum += lineTotal;
            }
        }
        return sum;
    }

    public static ArrayList<Order> filterOrdersByDate(Date fromDate, Date toDate)
    {
        ArrayList<Order> result = new ArrayList<>();
        ArrayList<Order> orders = getOrders();
        if (fromDate == null || toDate == null) {
            result.addAll(orders);
            return result;
        }

        Date start = fromDate.before(toDate) ? fromDate : toDate;
        Date end = fromDate.before(toDate) ? toDate : fromDate;
        for (Order order : orders) {
            Date orderDate = order.getOrderDate();
            if (orderDate == null) {
                continue;
            }
            if (!orderDate.before(start) && !orderDate.after(end)) {
                result.add(order);
            }
        }
        return result;
    }
}
