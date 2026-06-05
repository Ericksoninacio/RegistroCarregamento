package com.registrocarregamento.worker;

import android.content.Context;
import androidx.work.WorkerParameters;
import dagger.internal.DaggerGenerated;
import dagger.internal.InstanceFactory;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class SincronizacaoWorker_AssistedFactory_Impl implements SincronizacaoWorker_AssistedFactory {
  private final SincronizacaoWorker_Factory delegateFactory;

  SincronizacaoWorker_AssistedFactory_Impl(SincronizacaoWorker_Factory delegateFactory) {
    this.delegateFactory = delegateFactory;
  }

  @Override
  public SincronizacaoWorker create(Context p0, WorkerParameters p1) {
    return delegateFactory.get(p0, p1);
  }

  public static Provider<SincronizacaoWorker_AssistedFactory> create(
      SincronizacaoWorker_Factory delegateFactory) {
    return InstanceFactory.create(new SincronizacaoWorker_AssistedFactory_Impl(delegateFactory));
  }

  public static dagger.internal.Provider<SincronizacaoWorker_AssistedFactory> createFactoryProvider(
      SincronizacaoWorker_Factory delegateFactory) {
    return InstanceFactory.create(new SincronizacaoWorker_AssistedFactory_Impl(delegateFactory));
  }
}
