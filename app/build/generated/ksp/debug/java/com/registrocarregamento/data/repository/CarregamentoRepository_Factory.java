package com.registrocarregamento.data.repository;

import com.registrocarregamento.data.local.dao.CarregamentoDao;
import com.registrocarregamento.data.local.dao.FilaSincronizacaoDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast"
})
public final class CarregamentoRepository_Factory implements Factory<CarregamentoRepository> {
  private final Provider<CarregamentoDao> carregamentoDaoProvider;

  private final Provider<FilaSincronizacaoDao> filaSincronizacaoDaoProvider;

  public CarregamentoRepository_Factory(Provider<CarregamentoDao> carregamentoDaoProvider,
      Provider<FilaSincronizacaoDao> filaSincronizacaoDaoProvider) {
    this.carregamentoDaoProvider = carregamentoDaoProvider;
    this.filaSincronizacaoDaoProvider = filaSincronizacaoDaoProvider;
  }

  @Override
  public CarregamentoRepository get() {
    return newInstance(carregamentoDaoProvider.get(), filaSincronizacaoDaoProvider.get());
  }

  public static CarregamentoRepository_Factory create(
      Provider<CarregamentoDao> carregamentoDaoProvider,
      Provider<FilaSincronizacaoDao> filaSincronizacaoDaoProvider) {
    return new CarregamentoRepository_Factory(carregamentoDaoProvider, filaSincronizacaoDaoProvider);
  }

  public static CarregamentoRepository newInstance(CarregamentoDao carregamentoDao,
      FilaSincronizacaoDao filaSincronizacaoDao) {
    return new CarregamentoRepository(carregamentoDao, filaSincronizacaoDao);
  }
}
