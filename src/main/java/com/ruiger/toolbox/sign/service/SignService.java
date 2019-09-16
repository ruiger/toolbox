package com.ruiger.toolbox.sign.service;

/**
 * <p>Title: 签到</p>
 * <p> </p>
 * <p>Company: www.dhcc.com.cn</p>
 * <p>Email: yeyi@dhcc.com.cn</p>
 * <p>Copyright: Copyright (c) 2019</p>
 *
 * @author yeyi
 * @version 1.0
 * @date 2019/9/16
 **/
public interface SignService {

	/**
	 * 签到
	 * @param name
	 * @param password
	 * @return
	 */
	public String sign(String name,String password) throws Exception;
}
