package dev.tsnanh.vku.domain.repositories

import dev.tsnanh.vku.domain.entities.HasUserResponse
import dev.tsnanh.vku.domain.entities.Resource
import dev.tsnanh.vku.domain.handler.ErrorHandler
import dev.tsnanh.vku.domain.network.VKUServiceApi

interface UserRepo {
    //    suspend fun getUser(userId: String): Resource<User>
    suspend fun login(idToken: String): Resource<HasUserResponse>
}

class UserRepoImpl : UserRepo {
    override suspend fun login(idToken: String): Resource<HasUserResponse> =
        try {
            Resource.Success(VKUServiceApi.network.login(idToken))
        } catch (e: Throwable) {
            ErrorHandler.handleError(e)
        }
}