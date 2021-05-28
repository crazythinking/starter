package net.engining.zeebe.spring.client.ext.bean;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;

/**
 * 默认请求Header
 * @author luxue
 *
 */

public class DefaultRequestHeader implements Serializable{

	private static final long serialVersionUID = 1L;

	/**
	 * 唯一标识该交易的全局Id
	 */
	private String globalId;
	
	/**
	 * 服务提供系统标识
	 */
	@NotBlank
	private String svPrId;

	/**
	 * 渠道Id（请求系统标识）
	 */
	@NotBlank
	private String channelId;

	/**
	 * 请求交易流水号
	 */
	@NotBlank
	private String txnSerialNo;

	/**
	 * 交易请求时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	private Date timestamp;

	public String getGlobalId() {
		return globalId;
	}

	public void setGlobalId(String globalId) {
		this.globalId = globalId;
	}

	/**
	 * @return the svPrId
	 */
	public String getSvPrId() {
		return svPrId;
	}

	/**
	 * @param svPrId the svPrId to set
	 */
	public void setSvPrId(String svPrId) {
		this.svPrId = svPrId;
	}

	/**
	 * @return the channelId
	 */
	public String getChannelId() {
		return channelId;
	}

	/**
	 * @param channelId the channelId to set
	 */
	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

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
