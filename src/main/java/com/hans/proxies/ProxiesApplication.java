package com.hans.proxies;

import com.hans.proxies.employee.EmployeeService;
import com.hans.proxies.employee.EmployeeServiceProxy;
import java.lang.annotation.*;
import java.lang.reflect.Proxy;
import org.springframework.aot.hint.annotation.Reflective;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;

@SpringBootApplication
public class ProxiesApplication {

  public static void main(String[] args) {
    SpringApplication.run(ProxiesApplication.class, args);
  }

  @Bean
  @Order(1)
  ApplicationRunner employeeProxyRunner() {
    return args -> {
      EmployeeService service = new EmployeeServiceProxy();
      service.create();
    };
  }

  @Bean
  ApplicationRunner applicationRunner() {
    return args -> {
      var customerService = new DefaultCustomerService();
      var proxyInstance =
          (CustomerService)
              Proxy.newProxyInstance(
                  customerService.getClass().getClassLoader(),
                  customerService.getClass().getInterfaces(),
                  (proxy, method, args1) -> {
                    System.out.println(
                        "calling " + method.getName() + " with args [ " + args + " ]");
                    try {
                      if (method.getAnnotation(MyTransactional.class) != null) {
                        System.out.println("starting transaction !!");
                      }
                      return method.invoke(customerService, args1);
                    } finally{
                      if (method.getAnnotation(MyTransactional.class) != null) {
                        System.out.println("finished transaction !!");
                      }
                    }
                  });
      proxyInstance.create();
    };
  }
}

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Reflective
@Documented
@interface MyTransactional {}

class DefaultCustomerService implements CustomerService {

  @Override
  public void create() {
    System.out.println("create()");
  }

  @Override
  public void add() {
    System.out.println("add()");
  }
}

interface CustomerService {
  @MyTransactional
  void create();

  void add();
}
