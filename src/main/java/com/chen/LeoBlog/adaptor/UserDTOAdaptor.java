package com.chen.LeoBlog.adaptor;

import cn.hutool.core.bean.BeanUtil;
import com.chen.LeoBlog.dto.UserDTO;
import com.chen.LeoBlog.po.User;

public class UserDTOAdaptor {
    /**
     * 将User转化为UserDTO
     *
     * @param user
     * @return
     */
    public static UserDTO buildUserDTO(User user) {
        UserDTO userDTO = new UserDTO();
        BeanUtil.copyProperties(user, userDTO);
        return userDTO;
    }


}
