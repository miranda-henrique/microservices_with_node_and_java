package com.microservicesnodejava.productapi.config.interceptor;

import com.microservicesnodejava.productapi.config.exception.ValidationException;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

public class FeignClientAuthInterceptor implements RequestInterceptor {

  private static final String AUTHORIZATION = "Authorization";

  @Override
  public void apply(RequestTemplate template) {
    var currentRequest = getCurrentRequest();
    template
        .header("Authorization", currentRequest.getHeader(AUTHORIZATION));
  }

  private HttpServletRequest getCurrentRequest() {
    try {
      return ((ServletRequestAttributes) RequestContextHolder
          .getRequestAttributes())
          .getRequest();
    } catch (Exception e) {
      e.printStackTrace();
      throw new ValidationException("Current request could not be processed");
    }
  }
}
