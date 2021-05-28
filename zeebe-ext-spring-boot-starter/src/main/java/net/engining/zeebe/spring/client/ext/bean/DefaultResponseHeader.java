package net.engining.zeebe.spring.client.ext.bean;

import java.io.Serializable;
import java.util.Date;

/**
 * 默认返回Header
 * @author luxue
 *
 */
public class DefaultResponseHeader implements Serializable{

	private static final long serialVersionUID = 1L;
	
	/**
	 * 请求交易流水号
	 */
	private String txnSerialNo;

	/**
	 * 服务提供方返回流水号
	 */
	private String svPrSerialNo;
	
	/**
	 * 交易返回时间
	 */
	private Date timestamp = new Date();

	/**
	 * @return the txnSerialNo
	 */
	public String getTxnSerialNo() {
		return txnSerialNo;
	}

	/**
	 * @param txnSerialNo the txnSerialNo to set
	 */
	public void setTxnSerialNo(String txnSerialNo) {
		this.txnSerialNo = txnSerialNo;
	}

	/**
	 * @return the svPrSerialNo
	 */
	public String getSvPrSerialNo() {
		return svPrSerialNo;
	}

	/**
	 * @param svPrSerialNo the svPrSerialNo to set
	 */
	public void setSvPrSerialNo(String svPrSerialNo) {
		this.svPrSerialNo = svPrSerialNo;
	}

	/**
	 * @return the timestamp
	 */
	public Date getTimestamp() {
		return timestamp;
	}

	/**
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	
}
