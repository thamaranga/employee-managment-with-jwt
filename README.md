This is sample web application which demonstrates spring boot 
security with json web token.

First need to call /authenticate endpoint. If we have provided correct
credentials then it will return an access token and refresh token. Unless
it will return an exception.

Then using the access token we can access secured endpoints.

If we try to access a resource which we don't have permission to access, 
then it will return 403 error.

After access token is expired we will get 403 error code with Custom errorcode
100. Then we need to   generate a new access token by calling /generateNewAccessTokenUsingRefreshToken endpoint.

When refresh token also expired we have to again call /authenticate endpoint.


For testing purpose I can use below user credentials.
(I have already insereted below user details into employee db)

{

"userName":"thamaranga",
"password":"789123"

}


{

"userName":"prakash",
"password":"787878"

}

{

"userName":"hong",
"password":"121212"

}

In real time applications we can set validity of access token like 15 minutes and refresh token for 1 day.
