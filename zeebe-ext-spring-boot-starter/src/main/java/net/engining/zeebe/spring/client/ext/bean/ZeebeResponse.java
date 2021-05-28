package net.engining.zeebe.spring.client.ext.bean;

import com.google.common.collect.Maps;
import net.engining.pg.support.core.exception.ErrorCode;

import java.io.Serializable;
import java.util.Map;

/**
 * 通用的，Response
 * 支持自定义ResponseHead, 自定义ResponseData, 包含固定的, 由系统预先定义的返回码及返回描述;
 *
 * @author luxue
 *
 */
public class ZeebeResponse<H, T> implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/**
	 * 返回结果码
	 */
	private String statusCode = ErrorCode.Success.getValue();

	/**
	 * 返回结果描述
	 */
	private String statusDesc = ErrorCode.Success.getLabel();
	
	/**
	 * 返回头
	 */
	private H responseHead;

	/**
	 * 返回业务数据
	 */
	private T responseData;
	
	/**
	 * @return the statusCode
	 */
	public String getStatusCode() {
		return statusCode;
	}

	/**
	 * @param statusCode the statusCode to set
	 */
	public ZeebeResponse<H,T> setStatusCode(String statusCode) {
		this.statusCode = statusCode;
		return this;
	}

	/**
	 * @return the statusDesc
	 */
	public String getStatusDesc() {
		return statusDesc;
	}

	/**
	 * @param statusDesc the statusDesc to set
	 */
	public ZeebeResponse<H,T> setStatusDesc(String statusDesc) {
		this.statusDesc = statusDesc;
		return this;
	}
	
	/**
	 * @return the responseHead
	 */
	public H getResponseHead() {
		return responseHead;
	}

	/**
	 * @param responseHead the responseHead to set
	 */
	public ZeebeResponse<H,T> setResponseHead(H responseHead) {
		this.responseHead = responseHead;
		return this;
	}

	/**
	 * @return the responseData
	 */
	public T getResponseData() {
		return responseData;
	}

	/**
	 * @param responseData
	 *            the responseData to set
	 */
	public ZeebeResponse<H,T> setResponseData(T responseData) {
		this.responseData = responseData;
		return this;
	}

}
