package com.cwp.cloud.bean;

import java.util.List;

/**
 * 分页结果实体类
 * Created by chen_wp on 2019-09-23.
 */
public class PageResult<T> {

     private Status status;
     private int totalPages;
     private List<T> data;

     public Status getStatus() {
          return status;
     }

     public void setStatus(Status status) {
          this.status = status;
     }

     public int getTotalPages() {
          return totalPages;
     }

     public void setTotalPages(int totalPages) {
          this.totalPages = totalPages;
     }

     public List<T> getData() {
          return data;
     }

     public void setData(List<T> data) {
          this.data = data;
     }
}
