package com.registrocarregamento.di;

import com.registrocarregamento.data.local.AppDatabase;
import com.registrocarregamento.data.local.dao.CarregamentoDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class AppModule_ProvideCarregamentoDaoFactory implements Factory<CarregamentoDao> {
  private final Provider<AppDatabase> dbProvider;

  public AppModule_ProvideCarregamentoDaoFactory(Provider<AppDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public CarregamentoDao get() {
    return provideCarregamentoDao(dbProvider.get());
  }

  public static AppModule_ProvideCarregamentoDaoFactory create(Provider<AppDatabase> dbProvider) {
    return new AppModule_ProvideCarregamentoDaoFactory(dbProvider);
  }

  public static CarregamentoDao provideCarregamentoDao(AppDatabase db) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideCarregamentoDao(db));
  }
}
