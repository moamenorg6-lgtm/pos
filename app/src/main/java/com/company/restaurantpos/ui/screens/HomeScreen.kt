package com.company.restaurantpos.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.company.restaurantpos.R
import com.company.restaurantpos.ui.navigation.Screen

@Composable
fun HomeScreen(
    navController: NavController = rememberNavController()
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.home_screen_title),
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )
        Text(
            text = stringResource(R.string.home_screen_subtitle),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Navigation buttons
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = { navController.navigate(Screen.POS.route) },
                modifier = Modifier.fillMaxWidth(0.6f)
            ) {
                Icon(
                    imageVector = Icons.Default.PointOfSale,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.nav_pos))
            }
            
            OutlinedButton(
                onClick = { navController.navigate(Screen.Kitchen.route) },
                modifier = Modifier.fillMaxWidth(0.6f)
            ) {
                Icon(
                    imageVector = Icons.Default.Kitchen,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.nav_kitchen))
            }
            
            OutlinedButton(
                onClick = { navController.navigate(Screen.Admin.route) },
                modifier = Modifier.fillMaxWidth(0.6f)
            ) {
                Icon(
                    imageVector = Icons.Default.AdminPanelSettings,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.nav_admin))
            }
        }
    }
}