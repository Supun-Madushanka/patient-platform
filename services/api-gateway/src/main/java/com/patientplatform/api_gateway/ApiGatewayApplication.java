package com.patientplatform.api_gateway;

import com.patientplatform.api_gateway.security.AuthFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.server.mvc.filter.LoadBalancerFilterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

@SpringBootApplication
public class ApiGatewayApplication {

	@Autowired
	private AuthFilter authFilter;

	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayApplication.class, args);
	}

	@Bean
	public RouterFunction<ServerResponse> routes() {

		RouterFunction<ServerResponse> authRoutes =
				GatewayRouterFunctions.route("auth-service")
						.route(RequestPredicates.path("/api/auth/**"),
								HandlerFunctions.http())
						.filter(LoadBalancerFilterFunctions.lb("auth-service"))
						.filter(authFilter)
						.build();

		RouterFunction<ServerResponse> patientRoutes =
				GatewayRouterFunctions.route("patient-service")
						.route(RequestPredicates.path("/api/patients/**"),
								HandlerFunctions.http())
						.filter(LoadBalancerFilterFunctions.lb("patient-service"))
						.filter(authFilter)
						.build();

		RouterFunction<ServerResponse> doctorRoutes =
				GatewayRouterFunctions.route("doctor-service")
						.route(RequestPredicates.path("/api/doctors/**"),
								HandlerFunctions.http())
						.filter(LoadBalancerFilterFunctions.lb("doctor-service"))
						.filter(authFilter)
						.build();

		RouterFunction<ServerResponse> appointmentRoutes =
				GatewayRouterFunctions.route("appointment-service")
						.route(RequestPredicates.path("/api/appointments/**"),
								HandlerFunctions.http())
						.filter(LoadBalancerFilterFunctions.lb("appointment-service"))
						.filter(authFilter)
						.build();

		RouterFunction<ServerResponse> medicalRoutes =
				GatewayRouterFunctions.route("medical-records-service")
						.route(RequestPredicates.path("/api/records/**"),
								HandlerFunctions.http())
						.filter(LoadBalancerFilterFunctions.lb("medical-records-service"))
						.filter(authFilter)
						.build();

		RouterFunction<ServerResponse> billingRoutes =
				GatewayRouterFunctions.route("billing-service")
						.route(RequestPredicates.path("/api/invoices/**"),
								HandlerFunctions.http())
						.filter(LoadBalancerFilterFunctions.lb("billing-service"))
						.filter(authFilter)
						.build();

		RouterFunction<ServerResponse> notificationRoutes =
				GatewayRouterFunctions.route("notification-service")
						.route(RequestPredicates.path("/api/notifications/**"),
								HandlerFunctions.http())
						.filter(LoadBalancerFilterFunctions.lb("notification-service"))
						.filter(authFilter)
						.build();

		return authRoutes
				.and(patientRoutes)
				.and(doctorRoutes)
				.and(appointmentRoutes)
				.and(medicalRoutes)
				.and(billingRoutes)
				.and(notificationRoutes);
	}
}