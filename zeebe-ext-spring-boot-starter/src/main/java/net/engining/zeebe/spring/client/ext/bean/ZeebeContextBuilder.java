package net.engining.zeebe.spring.client.ext.bean;

/**
 * 重构，支持自定义RequestHead
 * 通用的Web Request Builder
 * @author luxue
 *
 */
public class ZeebeContextBuilder<H,T> {
	
	public ZeebeContext<H,T> build(){
		return new ZeebeContext<>();
	}
}
