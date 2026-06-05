package com.registrocarregamento.di;

import com.registrocarregamento.data.local.AppDatabase;
import com.registrocarregamento.data.local.dao.FilaSincronizacaoDao;
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
public final class AppModule_ProvideFilaSincronizacaoDaoFactory implements Factory<FilaSincronizacaoDao> {
  private final Provider<AppDatabase> dbProvider;

  public AppModule_ProvideFilaSincronizacaoDaoFactory(Provider<AppDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public FilaSincronizacaoDao get() {
    return provideFilaSincronizacaoDao(dbProvider.get());
  }

  public static AppModule_ProvideFilaSincronizacaoDaoFactory create(
      Provider<AppDatabase> dbProvider) {
    return new AppModule_ProvideFilaSincronizacaoDaoFactory(dbProvider);
  }

  public static FilaSincronizacaoDao provideFilaSincronizacaoDao(AppDatabase db) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideFilaSincronizacaoDao(db));
  }
}
