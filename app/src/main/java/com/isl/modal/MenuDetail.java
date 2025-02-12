package com.isl.modal;

import java.util.List;

public class MenuDetail {

    private String id;
    private String name;
    private String caption;
    private String menuLink;
    private List<String> rights;
    private List<MenuDetail> subMenu;

    public String getMenuLink() {
        return menuLink;
    }

    public void setMenuLink(String menuLink) {
        this.menuLink = menuLink;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public List<String> getRights() {
        return rights;
    }

    public void setRights(List<String> rights) {
        this.rights = rights;
    }

    public List<MenuDetail> getSubMenu() {
        return subMenu;
    }

    public void setSubMenu(List<MenuDetail> subMenu) {
        this.subMenu = subMenu;
    }
}
