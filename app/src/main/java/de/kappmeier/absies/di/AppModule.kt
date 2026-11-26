package de.kappmeier.absies.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.kappmeier.absies.core.ink.InkStore
import de.kappmeier.absies.core.ink.InMemoryInkStore
import de.kappmeier.absies.feature.math.MathExerciseGenerator
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideInkStore(): InkStore = InMemoryInkStore()

    @Provides
    @Singleton
    fun provideMathExerciseGenerator(): MathExerciseGenerator = MathExerciseGenerator()
}
