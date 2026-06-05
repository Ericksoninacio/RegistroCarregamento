package com.registrocarregamento.ui.screens;

import android.content.Context;
import com.registrocarregamento.data.repository.CarregamentoRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class RegistroViewModel_Factory implements Factory<RegistroViewModel> {
  private final Provider<Context> contextProvider;

  private final Provider<CarregamentoRepository> repositoryProvider;

  public RegistroViewModel_Factory(Provider<Context> contextProvider,
      Provider<CarregamentoRepository> repositoryProvider) {
    this.contextProvider = contextProvider;
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public RegistroViewModel get() {
    return newInstance(contextProvider.get(), repositoryProvider.get());
  }

  public static RegistroViewModel_Factory create(Provider<Context> contextProvider,
      Provider<CarregamentoRepository> repositoryProvider) {
    return new RegistroViewModel_Factory(contextProvider, repositoryProvider);
  }

  public static RegistroViewModel newInstance(Context context, CarregamentoRepository repository) {
    return new RegistroViewModel(context, repository);
  }
}
