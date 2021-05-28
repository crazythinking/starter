package net.engining.zeebe.spring.client.ext.bean;

/**
 * Response Builder
 * @author luxue
 *
 */
public class ZeebeResponseBuilder<H,T> {

	public ZeebeResponse<H,T> build(){
		return new ZeebeResponse<>();
	}

}
