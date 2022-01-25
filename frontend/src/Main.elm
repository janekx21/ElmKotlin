module Main exposing (..)

import Browser exposing (Document, UrlRequest)
import Browser.Navigation as Nav
import Element exposing (Element, fill, height, text, width)
import Element.Font as Font
import Html exposing (Html)
import Html.Attributes
import Route exposing (Route(..))
import Url exposing (Url)
import Views.HomeView as HomeView


main : Program () Model Msg
main =
    Browser.application
        { view = view
        , update = update
        , init = init
        , subscriptions = subscriptions
        , onUrlChange = UrlChanged
        , onUrlRequest = LinkClicked
        }



-- model


type alias Model =
    { route : Route
    , page : Page
    , navKey : Nav.Key
    }


type Page
    = NotFoundPage
    | HomePage HomeView.Model


init : () -> Url -> Nav.Key -> ( Model, Cmd Msg )
init _ url navKey =
    let
        model =
            { route = Route.parseUrl url
            , page = NotFoundPage
            , navKey = navKey
            }
    in
    initCurrentPage ( model, Cmd.none )


initCurrentPage : ( Model, Cmd Msg ) -> ( Model, Cmd Msg )
initCurrentPage ( model, existingCmds ) =
    let
        ( currentPage, mappedPageCmds ) =
            case model.route of
                Route.NotFoundRoute ->
                    ( NotFoundPage, Cmd.none )

                Route.HomeRoute ->
                    let
                        ( pageModel, pageCmds ) =
                            HomeView.init
                    in
                    ( HomePage pageModel, Cmd.map HomePageMsg pageCmds )
    in
    ( { model | page = currentPage }
    , Cmd.batch [ existingCmds, mappedPageCmds ]
    )



-- update


type Msg
    = HomePageMsg HomeView.Msg
    | LinkClicked UrlRequest
    | UrlChanged Url


update : Msg -> Model -> ( Model, Cmd Msg )
update msg model =
    case ( msg, model.page ) of
        ( HomePageMsg subMsg, HomePage pageModel ) ->
            let
                ( updatedPageModel, updatedCmd ) =
                    HomeView.update subMsg pageModel
            in
            ( { model | page = HomePage updatedPageModel }
            , Cmd.map HomePageMsg updatedCmd
            )

        ( LinkClicked urlRequest, _ ) ->
            case urlRequest of
                Browser.Internal url ->
                    ( model, Nav.pushUrl model.navKey (Url.toString url) )

                Browser.External url ->
                    ( model, Nav.load url )

        ( UrlChanged url, _ ) ->
            let
                newRoute =
                    Route.parseUrl url
            in
            ( { model | route = newRoute }, Cmd.none )
                |> initCurrentPage

        ( _, _ ) ->
            ( model, Cmd.none )



-- subscriptions


subscriptions : Model -> Sub Msg
subscriptions parentModel =
    case parentModel.page of
        HomePage model ->
            Sub.map HomePageMsg (HomeView.subscriptions model)

        NotFoundPage ->
            Sub.none



-- view


view : Model -> Document Msg
view parentModel =
    case parentModel.page of
        NotFoundPage ->
            Document "Not Found" [ notFoundView ]

        HomePage model ->
            HomeView.view model |> defaultLayout |> document "Home" HomePageMsg


defaultLayout : Element msg -> Html msg
defaultLayout =
    Element.layout
        [ width fill
        , height fill
        , Font.size 16
        , Font.family [ Font.typeface "IBM Plex Sans", Font.sansSerif ]
        ]


document : String -> (msg -> Msg) -> Html msg -> Document Msg
document name msg html =
    Document name [ fontLink, rebootLink, html |> Html.map msg ]


fontLink =
    Html.node "link" [ Html.Attributes.rel "stylesheet", Html.Attributes.href "https://fonts.googleapis.com/css2?family=IBM+Plex+Sans:wght@300;400;700&display=swap" ] []


rebootLink =
    Html.node "link" [ Html.Attributes.rel "stylesheet", Html.Attributes.href "https://cdn.jsdelivr.net/npm/bootstrap-reboot@4.5.6/reboot.css" ] []


notFoundView : Html msg
notFoundView =
    Element.layout [] <| text <| "not found"
