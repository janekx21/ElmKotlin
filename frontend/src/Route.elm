module Route exposing (..)

import UUID exposing (UUID)
import Url exposing (Url)
import Url.Parser exposing ((</>), Parser, custom, map, oneOf, parse, top)


type Route
    = HomeRoute
    | NotFoundRoute


parseUrl : Url -> Route
parseUrl url =
    case parse matchRoute url of
        Just route ->
            route

        Nothing ->
            NotFoundRoute


matchRoute : Parser (Route -> a) a
matchRoute =
    oneOf
        [ map HomeRoute top
        ]


uuid : Parser (UUID -> a) a
uuid =
    custom "UUID" (UUID.fromString >> Result.toMaybe)
