package com.deep.notify.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp

/**
 * Parses raw markdown text into an [AnnotatedString] applying styles for headers,
 * bold, italic, strikethrough, code snippets, lists, and links.
 * It keeps the markdown syntax symbols but applies a faded/subtle style to them.
 */
fun parseMarkdown(text: String, primaryColor: Color, outlineColor: Color): AnnotatedString {
    val builder = AnnotatedString.Builder(text)
    
    // 1. Headers (e.g., #, ##, ### at the start of a line)
    val headerRegex = Regex("(?m)^(#{1,6})\\s+(.+)$")
    headerRegex.findAll(text).forEach { match ->
        val level = match.groups[1]?.value?.length ?: 1
        val start = match.range.first
        val end = match.range.last + 1
        
        // Faint the # symbols
        builder.addStyle(
            style = SpanStyle(
                color = outlineColor.copy(alpha = 0.5f),
                fontWeight = FontWeight.Light
            ),
            start = start,
            end = start + level + 1
        )
        
        // Style the header text
        val fontSizeMultiplier = when (level) {
            1 -> 1.4f
            2 -> 1.25f
            else -> 1.15f
        }
        builder.addStyle(
            style = SpanStyle(
                fontWeight = FontWeight.Bold,
                fontSize = (16 * fontSizeMultiplier).sp
            ),
            start = start + level + 1,
            end = end
        )
    }

    // 2. Bold (**text** or __text__)
    val boldRegex = Regex("(\\*\\*|__)(.*?)\\1")
    boldRegex.findAll(text).forEach { match ->
        val start = match.range.first
        val end = match.range.last + 1
        val tagLen = match.groups[1]?.value?.length ?: 2
        
        // Faint the tags
        builder.addStyle(
            style = SpanStyle(color = outlineColor.copy(alpha = 0.4f)),
            start = start,
            end = start + tagLen
        )
        builder.addStyle(
            style = SpanStyle(color = outlineColor.copy(alpha = 0.4f)),
            start = end - tagLen,
            end = end
        )
        
        // Make the content bold
        builder.addStyle(
            style = SpanStyle(fontWeight = FontWeight.Bold),
            start = start + tagLen,
            end = end - tagLen
        )
    }

    // 3. Italic (*text* or _text_)
    val italicRegex = Regex("(?<!\\*)(\\*|_)(?!\\*)(.*?)\\1")
    italicRegex.findAll(text).forEach { match ->
        val start = match.range.first
        val end = match.range.last + 1
        val tagLen = match.groups[1]?.value?.length ?: 1
        
        // Faint the tags
        builder.addStyle(
            style = SpanStyle(color = outlineColor.copy(alpha = 0.4f)),
            start = start,
            end = start + tagLen
        )
        builder.addStyle(
            style = SpanStyle(color = outlineColor.copy(alpha = 0.4f)),
            start = end - tagLen,
            end = end
        )
        
        // Make content italic
        builder.addStyle(
            style = SpanStyle(fontStyle = FontStyle.Italic),
            start = start + tagLen,
            end = end - tagLen
        )
    }

    // 4. Strikethrough (~~text~~)
    val strikeRegex = Regex("(~~)(.*?)\\1")
    strikeRegex.findAll(text).forEach { match ->
        val start = match.range.first
        val end = match.range.last + 1
        
        // Faint the tags
        builder.addStyle(
            style = SpanStyle(color = outlineColor.copy(alpha = 0.4f)),
            start = start,
            end = start + 2
        )
        builder.addStyle(
            style = SpanStyle(color = outlineColor.copy(alpha = 0.4f)),
            start = end - 2,
            end = end
        )
        
        // Style content
        builder.addStyle(
            style = SpanStyle(textDecoration = TextDecoration.LineThrough),
            start = start + 2,
            end = end - 2
        )
    }

    // 5. Monospace code blocks and inline code
    // Inline code: `code`
    val codeRegex = Regex("(`)(.*?)\\1")
    codeRegex.findAll(text).forEach { match ->
        val start = match.range.first
        val end = match.range.last + 1
        
        // Faint the backticks
        builder.addStyle(
            style = SpanStyle(color = outlineColor.copy(alpha = 0.4f)),
            start = start,
            end = start + 1
        )
        builder.addStyle(
            style = SpanStyle(color = outlineColor.copy(alpha = 0.4f)),
            start = end - 1,
            end = end
        )
        
        // Style content
        builder.addStyle(
            style = SpanStyle(
                fontFamily = FontFamily.Monospace,
                background = primaryColor.copy(alpha = 0.1f),
                color = primaryColor
            ),
            start = start + 1,
            end = end - 1
        )
    }

    // Block code: ```code```
    val codeBlockRegex = Regex("(?s)(```)(.*?)\\1")
    codeBlockRegex.findAll(text).forEach { match ->
        val start = match.range.first
        val end = match.range.last + 1
        
        // Faint the block code tags
        builder.addStyle(
            style = SpanStyle(color = outlineColor.copy(alpha = 0.4f)),
            start = start,
            end = start + 3
        )
        builder.addStyle(
            style = SpanStyle(color = outlineColor.copy(alpha = 0.4f)),
            start = end - 3,
            end = end
        )
        
        // Style block content
        builder.addStyle(
            style = SpanStyle(
                fontFamily = FontFamily.Monospace,
                background = primaryColor.copy(alpha = 0.08f)
            ),
            start = start + 3,
            end = end - 3
        )
    }

    // 6. Bullet lists (- bullet, * bullet, etc.)
    val listRegex = Regex("(?m)^(\\s*[-*+])\\s+(.+)$")
    listRegex.findAll(text).forEach { match ->
        val start = match.range.first
        val tagEnd = match.groups[1]?.range?.last ?: start
        
        // Make the bullet point primary colored and bold
        builder.addStyle(
            style = SpanStyle(
                color = primaryColor,
                fontWeight = FontWeight.Bold
            ),
            start = start,
            end = tagEnd + 1
        )
    }

    // 7. Links / Images (e.g. ![alt](url) or [text](url))
    val linkRegex = Regex("(!?\\[)(.*?)\\]\\((.*?)\\)")
    linkRegex.findAll(text).forEach { match ->
        val start = match.range.first
        val end = match.range.last + 1
        
        builder.addStyle(
            style = SpanStyle(
                color = primaryColor,
                textDecoration = TextDecoration.Underline
            ),
            start = start,
            end = end
        )
    }

    return builder.toAnnotatedString()
}

/**
 * VisualTransformation that renders raw markdown formatting inline with rich styles
 * while editing notes inside a TextField.
 */
class MarkdownVisualTransformation(
    private val primaryColor: Color,
    private val outlineColor: Color
) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val parsed = parseMarkdown(text.text, primaryColor, outlineColor)
        return TransformedText(parsed, OffsetMapping.Identity)
    }
}
