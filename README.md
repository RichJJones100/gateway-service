# APIGatewayService
Springboot API Gateway Service using Spring Cloud Gateway, with circuit breaking and authentication.

![Spring Cloud Gateway Diagram](docs/spring_cloud_gateway_diagram.png)

## Getting Started

## Configuring a service
All services are configured in the application.yml file.

### Adding route

- ``ID`` - Unique identifier for the service.
- ``URI`` - Route to service
- Predictes: ``PATH`` - The incoming HTTP request path that will access the service.



```yaml
spring:
    cloud:
        gateway:
          routes:
            - id: example-service           
              uri: http://localhost:8081/
              predicates:                   
                - Path=/example/**        
```
***Note:** Route predicates (Route Predicate Factories) establish the attributes an incoming HTTP a request must match for the service to be routed to; this can be anything from the request, such as headers or parameters. Other factories options can be seen [here](https://cloud.spring.io/spring-cloud-static/spring-cloud-gateway/2.2.2.RELEASE/reference/html/), in section 5.* 

### Configuring Circuit Breaker
There are two options of how to configure a circuit breaker for the service. First option is to use the default configuration, and the second is use a custom breaker. Further details can be found in the [circuit breaker section](##Circuit Breaker).

#### Default Breaker
Adding the ``CircuitBreaker`` filter without arguments instructs the gateway to use the [default breaker settings](#Default Breaker).

```yaml
spring:
    cloud:
        gateway:
          routes:
            - id: example-service           
              uri: http://localhost:8081/
              predicates:                   
                - Path=/greeting/**
              filters:
                - CircuitBreaker
```
***Note:** Route filters (Gateway Filter Factories) allow the modification of the incoming HTTP request or outgoing HTTP response in some manner.*

#### Custom breaker
Adding a ``name`` argument to the circuit breaker filter, will instruct the gateway to use the custom breaker of its value. See [section](####Create Custom Breaker) below to create. This ``name`` argument can be set in either of the following ways:

```yaml
  filters:
      - CircuitBreaker=customBreakerA
```
OR
```yaml
  filters:
      - name: CircuitBreaker
        args:
          name: customBreakerA
```

#### Fallback
The circuit breaker as provides the option to set a fallback response, in instances where the service is unavailable. To set a fallback, add the additional argument of ``fallbackUri`` to the breaker with forwarding value. To create a custom fallback see section [Create Custom Fallback](####Create Custom Fallback). 
```yaml
  filters:
      - name: CircuitBreaker
        args:
          name: customBreakerA
          fallbackUri: forward:/fallback/default
```

## Circuit Breaker
All circuit breakers are configured in the application.yml file.

A CircuitBreaker can be in one of the three states:

- **CLOSED** – everything is fine, no short-circuiting involved
- **OPEN** – remote server is down, all requests to it are short-circuited
- **HALF_OPEN** – a configured amount of time since entering OPEN state has elapsed and CircuitBreaker allows requests to check if the remote service is back online

We can configure the following settings:

- the failure rate threshold above which the CircuitBreaker opens and starts short-circuiting calls
- the wait duration which defines how long the CircuitBreaker should stay open before it switches to half open
- the size of the ring buffer when the CircuitBreaker is half open or closed
- a custom CircuitBreakerEventListener which handles CircuitBreaker events
- a custom Predicate which evaluates if an exception should count as a failure and thus increase the failure rate

##### Configuration Properties:

|Property|Default|Description|
|----|---|-------|
|failureRateThreshold|50|Configures the failure rate threshold in percentage. When the failure rate is equal or greater than the threshold the CircuitBreaker transitions to open and starts short-circuiting calls.|
|slowCallRateThreshold|100|Configures a threshold in percentage. The CircuitBreaker considers a call as slow when the call duration is greater than slowCallDurationThreshold.|
#### Default Breaker
The 
#### Create Custom Breaker
All custom breakers are set under instances. 
#### Create Custom Fallback

### Authentication



