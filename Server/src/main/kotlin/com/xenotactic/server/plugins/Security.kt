package com.xenotactic.server.plugins

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.*

fun Application.configureSecurity() {
//    authentication {
//    		basic(name = "myauth1") {
//    			realm = "Ktor Server"
//    			validate { credentials ->
//    				if (credentials.name == credentials.password) {
//    					UserIdPrincipal(credentials.name)
//    				} else {
//    					null
//    				}
//    			}
//    		}
//
//    	    form(name = "myauth2") {
//    	        userParamName = "user"
//    	        passwordParamName = "password"
//    	        challenge {
//    	        	/**/
//    			}
//    	    }
//    	}
//
//    routing {
//        authenticate("myauth1") {
//            get("/protected/route/basic") {
//                val principal = call.principal<UserIdPrincipal>()!!
//                call.respondText("Hello ${principal.name}")
//            }
//        }
//        authenticate("myauth1") {
//            get("/protected/route/form") {
//                val principal = call.principal<UserIdPrincipal>()!!
//                call.respondText("Hello ${principal.name}")
//            }
//        }
//    }
}
