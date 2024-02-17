package com.alonso.active4j.example.http;

import io.activej.config.Config;
import io.activej.http.*;
import io.activej.inject.annotation.Provides;
import io.activej.inject.module.AbstractModule;
import io.activej.inject.module.Module;
import io.activej.launchers.http.MultithreadedHttpServerLauncher;
import io.activej.promise.Promise;
import io.activej.worker.annotation.Worker;
import jakarta.validation.constraints.NotNull;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import static io.activej.bytebuf.ByteBufStrings.wrapAscii;
import static io.activej.config.Config.ofSystemProperties;
import static io.activej.config.converter.ConfigConverters.ofInetSocketAddress;

public final class Main extends MultithreadedHttpServerLauncher {
	private static final int PORT = 8700;

	@Provides
	@Worker
	AsyncServlet mainServlet() {
		return RoutingServlet.create()
				.map(HttpMethod.GET,"/car/", request -> getAll(request)) // Query Params: Integer pageIndex, Integer pageSize
				.map(HttpMethod.GET,"/car/:id", request -> getById(request))
				.map(HttpMethod.GET,"/car/name/:name", request -> getByName(request))
				.map(HttpMethod.GET,"/car/brand/:name", request -> getByBrandName(request))
				.map(HttpMethod.GET,"/car/price-range/", request -> getByPriceRange(request)) // Query Params: Double startPrice, Double finalPrice
				.map(HttpMethod.GET,"/car/name/:name", request -> HttpResponse.ok200().withBody(wrapAscii(request.getPathParameter("name"))))
				.map(HttpMethod.POST,"/car", request -> HttpResponse.ok200().withBody(wrapAscii(request.getPathParameter("name"))))
				.map(HttpMethod.PUT,"/car/:id", request -> HttpResponse.ok200().withBody(wrapAscii(request.getPathParameter("name"))))
				.map(HttpMethod.DELETE,"/car/:id", request -> HttpResponse.ok200().withBody(wrapAscii(request.getPathParameter("name"))));
	}


	private Promise<HttpResponse> getAll(HttpRequest request) {
		String pageIndexString = request.getQueryParameter("pageIndex");
		String pageSizeString = request.getQueryParameter("pageSize");
		Integer pageIndex = Integer.parseInt(pageIndexString);
		Integer pageSize = Integer.parseInt(pageSizeString);

		String response = "Get ALL" + pageIndex + " with Page Size: " + pageSize + " ";

		if(pageIndexString != null && pageSizeString != null) {
			return Promise.of(HttpResponse.ok200().withBody(response.getBytes(StandardCharsets.UTF_8)));
		} else {
			return Promise.of(HttpResponse.ok200().withBody("FULL GET ALL".getBytes(StandardCharsets.UTF_8)));
		}
	}

	private Promise<HttpResponse> getById(HttpRequest request) {
		return Promise.of(HttpResponse.ok200().withBody(wrapAscii(request.getPathParameter("id"))));
	}

	private Promise<HttpResponse> getByName(HttpRequest request) {
		return Promise.of(HttpResponse.ok200().withBody(wrapAscii(request.getPathParameter("name"))));
	}
	private Promise<HttpResponse> getByBrandName(HttpRequest request) {
		return Promise.of(HttpResponse.ok200().withBody(wrapAscii(request.getPathParameter("name"))));
	}

	private Promise<HttpResponse> putById(HttpRequest request) {
		String id = request.getPathParameter("id");
		if(id != null && !id.isEmpty()) {
			return Promise.of(HttpResponse.ofCode(202).withBody(wrapAscii(request.getPathParameter("id"))));
		} else {
			return Promise.of(HttpResponse.ofCode(404).withBody(wrapAscii(request.getPathParameter("id"))));
		}
	}

	private Promise<HttpResponse> deleteById(HttpRequest request) {
		String id = request.getPathParameter("id");
		if(id != null && !id.isEmpty()) {
			return Promise.of(HttpResponse.ofCode(204).withBody(wrapAscii(request.getPathParameter("id"))));
		} else {
			return Promise.of(HttpResponse.ofCode(404).withBody(wrapAscii(request.getPathParameter("id"))));
		}
	}

	private Promise<HttpResponse> getByPriceRange(HttpRequest request) {
		@NotNull String startPrice = request.getQueryParameter("startPrice");
		@NotNull String finalPrice = request.getQueryParameter("finalPrice");
		Double startP = Double.parseDouble(startPrice);
		Double finalP = Double.parseDouble(finalPrice);

		String response = "PRICE-RANGE" + startP + "until" + finalP + " ";
		return Promise.of(HttpResponse.ok200().withBody(response.getBytes(StandardCharsets.UTF_8)));
	}

	@Override
	protected Module getOverrideModule() {
		return new AbstractModule() {
			@Provides
			Config config() {
				return Config.create()
						.with("http.listenAddresses", Config.ofValue(ofInetSocketAddress(), new InetSocketAddress(PORT)))
						.with("workers", "" + Runtime.getRuntime().availableProcessors())
						.overrideWith(ofSystemProperties("config"));
			}
		};
	}

	public static void main(String[] args) throws Exception {
		new Main().launch(args);
	}
}