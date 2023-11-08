package com.a2zsuvidhaa.`in`.util.apis

import java.io.IOException

object Exceptions {
     class ApiException(message: String) : IOException(message)
     class NoInternetException(message: String) : IOException(message)
     class UnAuthorizedException(message : String) : IOException(message)
     class InternalServerError(message : String) : IOException(message)
     class GeneralException(message : String = "Something went wrong! client side") : IOException(message)
     class DoubleTransactionException(message : String) : IOException(message)
     class AppInProgressException(message: String) : IOException(message)
     class RootException() : IOException("Rooted Device Found!")
     class SessionExpiredException(message: String) : IOException(message)
}
