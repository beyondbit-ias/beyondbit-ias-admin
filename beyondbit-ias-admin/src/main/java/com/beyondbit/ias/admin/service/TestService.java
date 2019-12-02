package com.beyondbit.ias.admin.service;

import com.beyondbit.ias.admin.entity.Test;
import com.beyondbit.ias.core.common.PageUtils;

import java.util.List;

public interface TestService {
    public int save(Test entity);

    public Test getByID(int id);

    public PageUtils getlist(int pageIndex, int pageSize);

    public int delete(int id);

    public int updateSequence(int sequence, int id);
}
