package com.registrocarregamento.worker;

import android.content.Context;
import androidx.work.WorkerParameters;
import com.registrocarregamento.data.repository.CarregamentoRepository;
import dagger.internal.DaggerGenerated;
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
public final class SincronizacaoWorker_Factory {
  private final Provider<CarregamentoRepository> repositoryProvider;

  public SincronizacaoWorker_Factory(Provider<CarregamentoRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  public SincronizacaoWorker get(Context context, WorkerParameters workerParams) {
    return newInstance(context, workerParams, repositoryProvider.get());
  }

  public static SincronizacaoWorker_Factory create(
      Provider<CarregamentoRepository> repositoryProvider) {
    return new SincronizacaoWorker_Factory(repositoryProvider);
  }

  public static SincronizacaoWorker newInstance(Context context, WorkerParameters workerParams,
      CarregamentoRepository repository) {
    return new SincronizacaoWorker(context, workerParams, repository);
  }
}
