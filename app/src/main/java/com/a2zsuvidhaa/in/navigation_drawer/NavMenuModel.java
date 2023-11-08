package com.a2zsuvidhaa.in.navigation_drawer;


import java.util.ArrayList;
import java.util.List;

public class NavMenuModel {
    public String menuTitle;
    public int menuIconDrawable;
    public List<SubMenuModel> subMenu;

    public NavMenuModel(String menuTitle, int menuIconDrawable) {
        this.menuTitle = menuTitle;
        this.menuIconDrawable = menuIconDrawable;
        this.subMenu = new ArrayList<>();
    }

    public NavMenuModel(String menuTitle, int menuIconDrawable, ArrayList<SubMenuModel> subMenu) {
        this.menuTitle = menuTitle;
        this.menuIconDrawable = menuIconDrawable;
        this.subMenu = new ArrayList<>();
        this.subMenu.addAll(subMenu);
    }

    public static class SubMenuModel {
        public String subMenuTitle;

        public SubMenuModel(String subMenuTitle) {
            this.subMenuTitle = subMenuTitle;
        }
    }
}
