package net.engining.zeebe.spring.client.ext.bean;

import com.google.common.collect.Maps;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Map;

/**
 * 通用的，带有消息头的Context；
 * 支持自定义requestHead， 自定义requestData，以及各个流程节点的context；
 * 
 * @author luxue
 *
 */
public class ZeebeContext<H, T> implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 请求头
	 */
	@NotNull
	private H requestHead;

	/**
	 * 请求业务数据
	 */
	@NotNull
	private T requestData;

	/**
	 * 流程运行中各个节点的上下文数据；以节点的TypeName为key
	 */
	private Map<String, Object> nodeContext;

	public ZeebeContext() {
		this.nodeContext = Maps.newHashMap();
	}

	/**
	 * @return the requestHead
	 */
	public H getRequestHead() {
		return requestHead;
	}

	/**
	 * @param requestHead the requestHead to set
	 */
	public ZeebeContext<H,T> setRequestHead(H requestHead) {
		this.requestHead = requestHead;
		return this;
	}

	/**
	 * @return the requestData
	 */
	public T getRequestData() {
		return requestData;
	}

	public void setNodeContext(Map<String, Object> nodeContext) {
		this.nodeContext = nodeContext;
	}

	/**
	 * @param requestData
	 *            the requestData to set
	 */
	public ZeebeContext<H,T> setRequestData(T requestData) {
		this.requestData = requestData;
		return this;
	}

	/**
	 * @return the additionalReqMap
	 */
	public Map<String, Object> getNodeContext() {
		return nodeContext;
	}

	public ZeebeContext<H,T> putAdditionalRepMap(String reqKey, Object reqBean) {
		add(reqKey, reqBean);
		return this;
	}

	private void add(String reqKey, Object reqBean) {
		this.nodeContext.put(reqKey, reqBean);
	}

}
