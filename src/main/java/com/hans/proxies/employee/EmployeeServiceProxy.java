package com.hans.proxies.employee;

public class EmployeeServiceProxy implements EmployeeService {

  EmployeeService actualService;

  public EmployeeServiceProxy() {
    this.actualService = new EmployeeServiceImpl();
  }

  @Override
  public void create() {
    System.out.println("Create employee through proxy!!");
    actualService.create();
  }
}
