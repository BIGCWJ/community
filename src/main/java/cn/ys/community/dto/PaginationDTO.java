package cn.ys.community.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 关于主页-分页所需元素的DTO类
 * 实现逻辑：
 * 若当前点击的page是第一页的时候，<隐藏
 * 当pages里包含1的时候,<<隐藏
 * page>1的时候，<显示
 * page为最后一页的时候，>隐藏
 * pages里包含最后一页的页面时候，>>隐藏
 */
@Data
public class PaginationDTO<T> {
    private List<T> data;
    private boolean showPrevious;
    private boolean showFirstPage;
    private boolean showNext;
    private boolean showEndPage;
    private Integer page;
    private List<Integer> pages = new ArrayList<>();
    private Integer totalPage;

    //设置所需元素值
    public void setPagination(Integer totalCount, Integer page, Integer size) {

        //计算总页数赋值给totalPage
        if (totalCount % size == 0) {
            totalPage = totalCount / size;
        } else {
            totalPage = totalCount / size + 1;
        }

        //限制page>totalPage||page<totalPage
        if(page<1){
            page=1;
        }

        if(page>totalPage){
            page = totalPage;
        }

        //对其赋值
        this.page = page;

        //往pages中添加page元素，最多为7个元素
        pages.add(page);
        for (int i = 1; i <= 3; i++) {
            //向前添加，从下标0处开始添加page-i
            if (page - i > 0) {
                pages.add(0,page - i);
            }
            //向page后面添加page+i
            if (page + i <= totalPage) {
                pages.add(page + i);
            }
        }

        //是否展示上一页
        if (page == 1) {
            showPrevious = false;
        } else {
            showPrevious = true;
        }
        //是否展示下一页
        if (page == totalPage) {
            showNext = false;
        } else {
            showNext = true;
        }
        //是否展示第一页
        if (pages.contains(1)) {
            showFirstPage = false;
        } else {
            showFirstPage = true;
        }
        //是否展示最后一页
        if (pages.contains(totalPage)) {
            showEndPage = false;
        } else {
            showEndPage = true;
        }


    }
}
