package com.acaris.features.knowledge_base.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.acaris.core.ui.components.DottedUploadBox
import com.acaris.features.knowledge_base.presentation.model.KnowledgeUiModel

@Composable
fun CategoryCard(
    category: String,
    documents: List<KnowledgeUiModel>,
    onUploadClick: () -> Unit,
    onReadClick: (KnowledgeUiModel) -> Unit,
    onEditClick: (KnowledgeUiModel) -> Unit,
    onDeleteClick: (KnowledgeUiModel) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = category,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (documents.isEmpty()) {
                Text(
                    text = "Belum ada dokumen di kategori ini.",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                DottedUploadBox(
                    text = "Tambah Dokumen $category",
                    onClick = onUploadClick
                )
            } else {
                documents.forEach { doc ->
                    DocumentItemRow(
                        doc = doc,
                        onReadClick = { onReadClick(doc) },
                        onEditClick = { onEditClick(doc) },
                        onDeleteClick = { onDeleteClick(doc) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}