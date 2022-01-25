module Views.HomeView exposing (..)

import Element exposing (Attribute, Element, text)


type alias Model =
    ()


init : ( Model, Cmd Msg )
init =
    ( (), Cmd.none )


type Msg
    = Never


view : Model -> Element Msg
view model =
    text "Home"


update : Msg -> Model -> ( Model, Cmd Msg )
update msg model =
    ( model, Cmd.none )


subscriptions : Model -> Sub Msg
subscriptions model =
    Sub.none
