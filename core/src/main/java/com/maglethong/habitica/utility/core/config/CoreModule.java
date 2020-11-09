package com.maglethong.habitica.utility.core.config;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.maglethong.habitica.utility.core.habitica.api.IHabiticaClientService;
import com.maglethong.habitica.utility.core.habitica.internal.HabiticaClientService;


public class CoreModule extends AbstractModule {

	private static Injector injector = Guice.createInjector(new CoreModule());

	public static <I> I getInstance(Class<I> base) {
		return injector.getInstance(base);
	}

	@Override
	protected void configure() {
		// Habitica Service
		bind(IHabiticaClientService.class).to(HabiticaClientService.class);
	}

}

