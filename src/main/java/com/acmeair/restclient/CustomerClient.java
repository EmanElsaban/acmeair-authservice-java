/*******************************************************************************
 * Copyright (c) 2018 IBM Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

package com.acmeair.restclient;

import javax.enterprise.context.ApplicationScoped;

import java.io.IOException;
import java.time.temporal.ChronoUnit;

import javax.ws.rs.Consumes;
import javax.ws.rs.PathParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.core.MediaType;

@Path("/internal")
@RegisterRestClient(configKey="customerClient")
@ApplicationScoped
public interface CustomerClient {

  @GET
  @Path("/validateid/{login}/{password}")
  @Produces(MediaType.APPLICATION_JSON)
  @Timeout(500) 
  @CircuitBreaker(requestVolumeThreshold=4,failureRatio=0.5,successThreshold=10,delay=1,delayUnit=ChronoUnit.SECONDS)
  @Retry(maxRetries = 3, delayUnit = ChronoUnit.SECONDS, delay = 5, durationUnit = ChronoUnit.SECONDS, 
    maxDuration = 30,retryOn = Exception.class, abortOn = IOException.class)
  @Fallback(LoginFallbackHandler.class)
  public LoginResponse validateCustomer(
      @PathParam("login") String login, 
      @PathParam("password") String password);
}
