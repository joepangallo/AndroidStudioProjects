// Declares the package (namespace) this file belongs to.
package com.example.intent

// ---- Imports: each line brings in one class or function used below ----
import android.os.Bundle                                  // Holds saved UI state handed to onCreate().
import androidx.activity.ComponentActivity                // Base class for an Activity that hosts Compose UI.
import androidx.activity.compose.setContent               // Attaches a Compose UI tree to the Activity.
import androidx.activity.enableEdgeToEdge                  // Lets the app draw behind the system bars.
import androidx.compose.foundation.layout.Column          // Layout that stacks its children vertically.
import androidx.compose.foundation.layout.fillMaxSize     // Modifier: fill all available space.
import androidx.compose.foundation.layout.padding         // Modifier: add empty space around a composable.
import androidx.compose.material3.Button                   // Material 3 clickable button.
import androidx.compose.material3.Scaffold                 // Material 3 screen frame that reports system-bar insets.
import androidx.compose.material3.Text                     // Composable that displays text.
import androidx.compose.runtime.Composable                 // Annotation marking a function as Compose UI.
import androidx.compose.ui.Modifier                        // Used to size/position/decorate composables.
import androidx.compose.ui.tooling.preview.Preview         // Renders a composable in the IDE design pane.
import androidx.navigation3.runtime.NavKey                 // Marker interface every navigation key implements.
import androidx.navigation3.runtime.entryProvider          // DSL that maps each key type to its screen UI.
import androidx.navigation3.runtime.rememberNavBackStack   // Creates a back stack that survives rotation/process death.
import androidx.navigation3.ui.NavDisplay                   // Composable that shows the screen for the top key.
import com.example.intent.ui.theme.IntentTheme             // This app's Material theme (colors + fonts).
import kotlinx.serialization.Serializable                 // Lets Nav3 save/restore keys by serializing them.
import android.util.Log

// ===========================================================================
// NAVIGATION KEYS
// A "key" identifies a screen AND carries that screen's arguments. The back stack
// is just a list of keys — whichever key is on TOP is the screen currently shown.
// Navigating forward = add a key; going back = remove the top key.
// ===========================================================================

@Serializable                                             // Generates a serializer so Nav3 can save this key.
data object HomeKey : NavKey                               // Home screen's key. No arguments, so one shared object is enough.

@Serializable                                             // Makes DetailKey saveable across config changes / process death.
data class DetailKey(val message: String) : NavKey        // Detail screen's key; `message` is its argument (replaces an Intent extra).

private const val tag = "LT"
/**
 * MainActivity — the app's single Activity. It hosts the navigation back stack and
 * swaps composable screens, instead of starting new Activities with Intents.
 */
class MainActivity : ComponentActivity() {                // Our Activity extends ComponentActivity to enable Compose.
    override fun onCreate(savedInstanceState: Bundle?) {  // Runs once, when the Activity is first created.
        super.onCreate(savedInstanceState)                // Let the base class do its own setup first.
        enableEdgeToEdge()                                // Allow drawing under the status/navigation bars.
        setContent {                                      // Everything inside here is this Activity's Compose UI.
            IntentTheme {                                 // Apply the app theme to all composables below.
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->  // Full-screen frame; hands us safe-area padding.
                    AppNavigation(modifier = Modifier.padding(innerPadding))   // Show the navigation, inset by that padding.
                }                                         // End Scaffold content lambda.
            }                                             // End IntentTheme.
        }                                                 // End setContent.
    }                                                     // End onCreate.
}                                                         // End MainActivity.

/**
 * AppNavigation — owns the back stack and declares which composable to draw for each key.
 * This is the heart of the navigation.
 */
@Composable                                               // This is a Compose UI function.
fun AppNavigation(modifier: Modifier = Modifier) {        // Accepts a Modifier (defaults to none) for layout/padding.
    val backStack = rememberNavBackStack(HomeKey)         // The back stack; starts with HomeKey and is remembered across recomposition/rotation.
    Log.d("LT"," In function AppNavigation")
    NavDisplay(                                           // NavDisplay draws the screen for the top key and reacts to back-stack changes.
        backStack = backStack,                            // The list of keys it observes.
        modifier = modifier,                              // Apply the padding/layout passed down from MainActivity.
        onBack = { backStack.removeLastOrNull() },        // System/predictive back: pop the top key to go back one screen.
        entryProvider = entryProvider {                   // Begin the map of key type -> screen UI.
            entry<HomeKey> {                              // If the top key is a HomeKey, show this:
                HomeScreen(                               // Draw the Home screen.
                    onOpenDetail = { backStack.add(DetailKey("Hello from Home!")) }  // Push a DetailKey (with its message) = navigate forward.
                )                                         // End HomeScreen call.
            }                                             // End HomeKey entry.
            entry<DetailKey> { key ->                     // If the top key is a DetailKey, receive it as `key` and show:
                DetailScreen(                             // Draw the Detail screen.
                    message = key.message,                // Read the argument straight off the key (replaces getStringExtra).
                    onBack = { backStack.removeLastOrNull() }  // Back button pops this key, returning to Home.
                )                                         // End DetailScreen call.
            }                                             // End DetailKey entry.
        }                                                 // End entryProvider map.
    )                                                 // End NavDisplay.

}                                                         // End AppNavigation.

/** HomeScreen — a greeting plus a button that navigates to the detail screen. */
@Composable                                               // Compose UI function.
fun HomeScreen(onOpenDetail: () -> Unit, modifier: Modifier = Modifier) {  // Takes a click callback and an optional Modifier.
    Column(modifier = modifier) {                         // Stack the children vertically.
        Text(text = "Hello Android!")                     // Show a greeting label.
        Button(onClick = onOpenDetail) {                  // A button; tapping it runs onOpenDetail (navigates forward).
            Text("Open Detail")                           // The button's label.
        }                                                 // End Button.
    }                                                     // End Column.
}                                                         // End HomeScreen.

/** DetailScreen — shows the message carried by DetailKey, plus a Back button. */
@Composable                                               // Compose UI function.
fun DetailScreen(message: String, onBack: () -> Unit, modifier: Modifier = Modifier) {  // Takes the message, a back callback, and an optional Modifier.
    Column(modifier = modifier) {                         // Stack the children vertically.
        Text(text = message)                              // Display the passed-in message.
        Button(onClick = onBack) {                        // A button; tapping it runs onBack (pops the stack).
            Text("Back")                                  // The button's label.
        }                                                 // End Button.
    }                                                     // End Column.
}                                                         // End DetailScreen.

@Preview(showBackground = true)                           // Render the next function in the IDE preview, with a background.
@Composable                                               // Compose UI function (preview only).
fun HomeScreenPreview() {                                 // A preview wrapper for HomeScreen.
    IntentTheme {                                         // Apply the theme so the preview matches the app.
        HomeScreen(onOpenDetail = {})                     // Show HomeScreen with a do-nothing click (previews don't navigate).
    }                                                     // End IntentTheme.
}                                                         // End HomeScreenPreview.

@Preview(showBackground = true)                           // Render the next function in the IDE preview.
@Composable                                               // Compose UI function (preview only).
fun DetailScreenPreview() {                               // A preview wrapper for DetailScreen.
    IntentTheme {                                         // Apply the theme.
        DetailScreen(message = "Preview message", onBack = {})  // Show DetailScreen with sample text and a no-op back.
    }                                                     // End IntentTheme.
}                                                         // End DetailScreenPreview.
