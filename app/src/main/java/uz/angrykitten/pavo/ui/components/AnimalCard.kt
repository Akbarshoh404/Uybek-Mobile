package uz.angrykitten.pavo.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import uz.angrykitten.pavo.data.model.Animal
import uz.angrykitten.pavo.ui.theme.AccentAdopt
import uz.angrykitten.pavo.ui.theme.AccentPet
import uz.angrykitten.pavo.ui.theme.Brand
import uz.angrykitten.pavo.ui.theme.LocalDarkTheme
import java.text.NumberFormat
import java.util.Locale

private val AnimalCardShape = RoundedCornerShape(28.dp)
private val AnimalImageShape = RoundedCornerShape(24.dp)

fun formatPrice(price: Double, currency: String): String {
    val formatter = NumberFormat.getNumberInstance(Locale.US)
    val formatted = formatter.format(price).replace(",", " ")
    return "$formatted $currency"
}

@Composable
fun ListingTypeBadge(listingType: String, modifier: Modifier = Modifier, compact: Boolean = false) {
    val badgeColor = when (listingType) {
        "sale" -> Brand
        "adoption" -> AccentPet
        "stud" -> AccentAdopt
        else -> Brand
    }
    val badgeText = when (listingType) {
        "sale" -> "SOTUV"
        "adoption" -> "ASRAB OLISH"
        "stud" -> "JUFTLASH"
        else -> "SOTUV"
    }
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(if (compact) 12.dp else 14.dp),
        color = badgeColor.copy(alpha = 0.14f),
        contentColor = badgeColor
    ) {
        Text(
            text = badgeText,
            modifier = Modifier.padding(horizontal = if (compact) 8.dp else 10.dp, vertical = 5.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun priceTint(): Color {
    return if (LocalDarkTheme.current) MaterialTheme.colorScheme.primary else Brand
}

@Composable
fun AnimalCard(
    animal: Animal,
    isSaved: Boolean,
    onCardClick: () -> Unit,
    onToggleSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.985f else 1f,
        animationSpec = tween(180),
        label = "animalCardScale"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onCardClick
            ),
        shape = AnimalCardShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Box {
                AsyncImage(
                    model = animal.images.firstOrNull(),
                    contentDescription = animal.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1.42f)
                        .clip(AnimalImageShape),
                    contentScale = ContentScale.Crop
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .padding(10.dp)
                ) {
                    ListingTypeBadge(
                        listingType = animal.listing_type,
                        modifier = Modifier.align(Alignment.TopStart)
                    )
                    SaveButton(
                        isSaved = isSaved,
                        onToggleSave = onToggleSave,
                        modifier = Modifier.align(Alignment.TopEnd)
                    )
                }

                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(10.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f)
                ) {
                    Text(
                        text = if (animal.listing_type == "adoption") "BEPUL" else formatPrice(animal.price, animal.currency),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = priceTint()
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = animal.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${animal.district_name}, ${animal.city_name}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AnimalStat(Icons.Default.Pets, animal.breed)
                AnimalStat(Icons.Default.Cake, "${animal.age_months} oy")
                AnimalStat(Icons.Default.Scale, "${animal.weight_kg} kg")
            }

            HorizontalDivider(
                modifier = Modifier.padding(top = 14.dp),
                color = MaterialTheme.colorScheme.outlineVariant
            )
        }
    }
}

@Composable
fun AnimalGridCard(
    animal: Animal,
    isSaved: Boolean,
    onCardClick: () -> Unit,
    onToggleSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onCardClick),
        shape = AnimalCardShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Box {
                AsyncImage(
                    model = animal.images.firstOrNull(),
                    contentDescription = animal.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(0.92f)
                        .clip(AnimalImageShape),
                    contentScale = ContentScale.Crop
                )
                ListingTypeBadge(
                    listingType = animal.listing_type,
                    compact = true,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                )
                SaveButton(
                    isSaved = isSaved,
                    onToggleSave = onToggleSave,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = if (animal.listing_type == "adoption") "BEPUL" else formatPrice(animal.price, animal.currency),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Black,
                color = priceTint(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = animal.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = animal.district_name,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun SaveButton(
    isSaved: Boolean,
    onToggleSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onToggleSave,
        modifier = modifier
            .size(38.dp)
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.94f), CircleShape)
    ) {
        Icon(
            imageVector = if (isSaved) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
            contentDescription = "Save",
            modifier = Modifier.size(18.dp),
            tint = if (isSaved) Brand else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun AnimalStat(icon: ImageVector, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(15.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun AnimalListCard(
    animal: Animal,
    isSaved: Boolean,
    onCardClick: () -> Unit,
    onToggleSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onCardClick),
        shape = AnimalCardShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AsyncImage(
                model = animal.images.firstOrNull(),
                contentDescription = animal.title,
                modifier = Modifier
                    .size(92.dp)
                    .clip(RoundedCornerShape(20.dp)),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (animal.listing_type == "adoption") "BEPUL" else formatPrice(animal.price, animal.currency),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = priceTint()
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = animal.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${animal.district_name}, ${animal.city_name}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            SaveButton(
                isSaved = isSaved,
                onToggleSave = onToggleSave
            )
        }
    }
}
