package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CricProduct
import com.example.ui.viewmodel.CricketViewModel

@Composable
fun GinnMartTab(viewModel: CricketViewModel) {
    val products by viewModel.storeProducts.collectAsState()
    val cart by viewModel.cartItems.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()

    var showCartDialog by remember { mutableStateOf(false) }
    var orderSuccess by remember { mutableStateOf(false) }
    var shippingAddress by remember { mutableStateOf("South Mumbai, Oval Maidan Road") }

    val filteredProducts = remember(selectedCategory, products) {
        if (selectedCategory == null) products else products.filter { it.category == selectedCategory }
    }

    val cartCount = cart.values.sum()
    val cartTotal = products.filter { cart.containsKey(it.id) }.sumOf { it.price * (cart[it.id] ?: 0) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SportColors.DarkBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Header Area
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "GinnMart Sports Shop",
                        color = SportColors.TextPrimary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "Premium cricket wear, bats, and protective guards.",
                        color = SportColors.TextSecondary,
                        fontSize = 11.sp
                    )
                }

                // Floating Cart Indicator on Top
                IconButton(
                    onClick = { if (cartCount > 0) showCartDialog = true },
                    modifier = Modifier
                        .background(SportColors.SoftCardBg, CircleShape)
                        .border(1.dp, SportColors.CardBorder, CircleShape)
                ) {
                    BadgedBox(
                        badge = {
                            if (cartCount > 0) {
                                Badge(containerColor = SportColors.BrightOrange) {
                                    Text(cartCount.toString(), color = Color.White)
                                }
                            }
                        }
                    ) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = "Cart", tint = SportColors.TextPrimary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Category filters buttons
            val categories = listOf(null, "Bat", "Ball", "Pad", "Helmet", "Gloves")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 4.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                categories.forEach { cat ->
                    val isSelected = selectedCategory == cat
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                if (isSelected) SportColors.ActiveBlue else SportColors.SoftCardBg
                            )
                            .border(
                                width = 1.dp,
                                color = if (isSelected) Color.Transparent else SportColors.CardBorder,
                                shape = RoundedCornerShape(14.dp)
                            )
                            .clickable { viewModel.selectCategory(cat) }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = cat ?: "All Gears",
                            color = if (isSelected) Color.White else SportColors.TextPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Products grid listing
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                filteredProducts.forEach { prod ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = SportColors.SoftCardBg)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Left product illustration box
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        if (prod.category == "Bat") Color(0xFFFFE4C4) else Color(
                                            0xFFFFCDD2
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (prod.category == "Bat") {
                                    CustomCricketBatGraphic(
                                        modifier = Modifier.size(54.dp),
                                        woodColor = Color(0xFFC68B59),
                                        gripColor = SportColors.ActiveBlue
                                    )
                                } else {
                                    // Leather Ball or Guard icons
                                    Icon(
                                        imageVector = if (prod.category == "Ball") Icons.Default.SportsBaseball else Icons.Default.Shield,
                                        contentDescription = null,
                                        tint = if (prod.category == "Ball") Color.Red else SportColors.GoldYellow,
                                        modifier = Modifier.size(36.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            // Details
                            Column(modifier = Modifier.weight(1.2f)) {
                                if (prod.isHot) {
                                    Badge(containerColor = SportColors.BrightOrange, modifier = Modifier.padding(bottom = 2.dp)) {
                                        Text("BEST SELLER", fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                Text(
                                    text = prod.name,
                                    color = SportColors.TextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = prod.desc,
                                    color = SportColors.TextSecondary,
                                    fontSize = 10.sp,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "₹${prod.price.toInt()}",
                                        color = SportColors.ActiveBlue,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "₹${prod.originalPrice.toInt()}",
                                        color = SportColors.TextSecondary.copy(alpha = 0.6f),
                                        fontSize = 11.sp,
                                        textDecoration = TextDecoration.LineThrough
                                    )
                                }
                            }

                            // Add to cart controls
                            val qty = cart.getOrDefault(prod.id, 0)
                            if (qty == 0) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(SportColors.SportGreen)
                                        .clickable { viewModel.addToCart(prod.id) }
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                                ) {
                                    Text("Add", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            } else {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(SportColors.CardBorder)
                                            .clickable { viewModel.removeFromCart(prod.id) },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("-", color = SportColors.TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Text(
                                        qty.toString(),
                                        color = SportColors.TextPrimary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(SportColors.SportGreen)
                                            .clickable { viewModel.addToCart(prod.id) },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("+", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(140.dp))
        }

        // Cart Checkout dialog overlay
        if (showCartDialog) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.7f))
                    .clickable { showCartDialog = false },
                contentAlignment = Alignment.BottomCenter
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = false) {},
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                    colors = CardDefaults.cardColors(containerColor = SportColors.SoftCardBg)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Locked GinnMart Cart",
                                color = SportColors.TextPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            IconButton(onClick = { showCartDialog = false }) {
                                Icon(Icons.Default.Close, contentDescription = "Close", tint = SportColors.TextPrimary)
                            }
                        }

                        Divider(color = SportColors.CardBorder, modifier = Modifier.padding(vertical = 10.dp))

                        // Checkout Cart list
                        Column(
                            modifier = Modifier.heightIn(max = 140.dp).verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            products.filter { cart.containsKey(it.id) }.forEach { item ->
                                val qty = cart[item.id] ?: 0
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(item.name, color = SportColors.TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        Text("₹${item.price} × $qty", color = SportColors.TextSecondary, fontSize = 10.sp)
                                    }
                                    Text("₹${item.price * qty}", color = SportColors.ActiveBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Divider(color = SportColors.CardBorder, modifier = Modifier.padding(vertical = 10.dp))

                        // Shipping details
                        Text("Shipping Academy Address", color = SportColors.TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        OutlinedTextField(
                            value = shippingAddress,
                            onValueChange = { shippingAddress = it },
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = SportColors.TextPrimary,
                                unfocusedTextColor = SportColors.TextPrimary,
                                focusedBorderColor = SportColors.ActiveBlue,
                                unfocusedBorderColor = SportColors.CardBorder,
                                focusedContainerColor = SportColors.DarkBackground,
                                unfocusedContainerColor = SportColors.DarkBackground
                            ),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("CART SUBTOTAL", color = SportColors.TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("₹$cartTotal", color = SportColors.GoldYellow, fontWeight = FontWeight.Black, fontSize = 16.sp)
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        GradientButton(
                            onClick = {
                                viewModel.clearCart()
                                showCartDialog = false
                                orderSuccess = true
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            testTag = "submit_button",
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Place Order & Pay 💳", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Order success popup widget
        if (orderSuccess) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.8f))
                    .clickable { orderSuccess = false },
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(0.85f),
                    colors = CardDefaults.cardColors(containerColor = SportColors.SoftCardBg)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                                .background(SportColors.SportGreen),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(36.dp))
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            "Order Placed Successfully! 🎉",
                            color = SportColors.TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Your cricket gear was packed and sent to $shippingAddress. Delivery is slated in 2 days.",
                            color = SportColors.TextSecondary,
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        GradientButton(
                            onClick = { orderSuccess = false },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Continue Shopping", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
