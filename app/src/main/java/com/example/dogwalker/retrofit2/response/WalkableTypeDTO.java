package com.example.dogwalker.retrofit2.response;

public class WalkableTypeDTO {
//    Boolean bolSizeS;
//    Boolean bolSizeM;
//    Boolean bolSizeL;
    int walkable_type_s;
    int walkable_type_m;
    int walkable_type_l;

    public WalkableTypeDTO(int walkable_type_s, int walkable_type_m, int walkable_type_l) {
        this.walkable_type_s = walkable_type_s;
        this.walkable_type_m = walkable_type_m;
        this.walkable_type_l = walkable_type_l;
    }

    public int getWalkable_type_s() {
        return walkable_type_s;
    }

    public void setWalkable_type_s(int walkable_type_s) {
        this.walkable_type_s = walkable_type_s;
    }

    public int getWalkable_type_m() {
        return walkable_type_m;
    }

    public void setWalkable_type_m(int walkable_type_m) {
        this.walkable_type_m = walkable_type_m;
    }

    public int getWalkable_type_l() {
        return walkable_type_l;
    }

    public void setWalkable_type_l(int walkable_type_l) {
        this.walkable_type_l = walkable_type_l;
    }
}
