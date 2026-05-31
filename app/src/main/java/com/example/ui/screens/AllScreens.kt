package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.theme.*
import com.example.viewmodel.KeysStatus
import com.example.viewmodel.SwitchRomViewModel

@Composable
fun DashboardScreen(
    viewModel: SwitchRomViewModel,
    onNavigate: (String) -> Unit
) {
    val specs by viewModel.hardwareSpecs.collectAsState()
    val romsList by viewModel.roms.collectAsState()
    val keysStatus by viewModel.keysStatus.collectAsState()
    val driversList by viewModel.drivers.collectAsState()
    
    val totalRoms = romsList.size
    val totalDriversDownloaded = driversList.count { it.isDownloaded }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("dashboard_root"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            SwitchBannerAnim()
        }

        // Hardware details
        item {
            specs?.let { spec ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, BorderSlate, RoundedCornerShape(16.dp))
                        .testTag("specs_card"),
                    colors = CardDefaults.cardColors(containerColor = DarkGreySurface),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "ANÁLISE DE HARDWARE ANDROID",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonBlue,
                                letterSpacing = 1.sp
                            )
                            Box(
                                modifier = Modifier
                                    .background(
                                        if (spec.isCompatibleWithDrivers) TextAccent.copy(alpha = 0.15f) else WarningYellow.copy(alpha = 0.15f),
                                        RoundedCornerShape(8.dp)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = if (spec.isCompatibleWithDrivers) "Vulkan Turnip Okey" else "Mali SoC Genérico",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (spec.isCompatibleWithDrivers) TextAccent else WarningYellow
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        HorizontalDivider(color = BorderSlate, thickness = 1.dp)
                        
                        Spacer(modifier = Modifier.height(12.dp))

                        // CPU line
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Settings, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "SoC / CPU: ", fontSize = 12.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
                            Text(text = spec.cpuModel, fontSize = 12.sp, color = TextPrimary, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // GPU / Driver Compatibility line
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Unidade GPU: ", fontSize = 12.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
                            Text(text = spec.gpuType, fontSize = 12.sp, color = TextPrimary, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // RAM
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Memória RAM Total: ", fontSize = 12.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
                            Text(text = "${spec.ramGb} GB LPDDR", fontSize = 12.sp, color = TextPrimary, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Storage and System details
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Build, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Espaço Livre: ", fontSize = 12.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
                            Text(text = "${spec.freeStorageGb} GB salvos ", fontSize = 12.sp, color = TextPrimary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Action pathways
        item {
            Text(
                text = "FERRAMENTAS PRINCIPAIS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        item {
            SwitchCardAction(
                title = "Conversor de ROMs (.nsz / .xci / .zip)",
                desc = "Transforme arquivos comprimidos ou de cartucho para NSP limpa rodável perfeitamente em segundos.",
                icon = { Icon(Icons.Default.Refresh, contentDescription = null, tint = NeonBlue) },
                accentColor = NeonBlue,
                testTag = "action_convert_rom",
                onClick = { onNavigate("converter") }
            )
        }

        item {
            SwitchCardAction(
                title = "Otimizador de Frames (Gerador de INI)",
                desc = "Crie arquivos de calibração para forçar 60FPS constantes e habilitar motor NCE por jogo.",
                icon = { Icon(Icons.Default.PlayArrow, contentDescription = null, tint = NeonRed) },
                accentColor = NeonRed,
                testTag = "action_optimize_frames",
                onClick = { onNavigate("optimizer") }
            )
        }

        item {
            SwitchCardAction(
                title = "Gerenciador do Sistema de Keys",
                desc = when (keysStatus) {
                    is KeysStatus.Valid -> "Chaves prod.keys carregadas e funcionais."
                    else -> "Instale suas chaves de decodificação prod.keys para desencriptar os cabeçalhos de jogos novos."
                },
                icon = { Icon(Icons.Default.Lock, contentDescription = null, tint = WarningYellow) },
                accentColor = WarningYellow,
                testTag = "action_keys_hub",
                onClick = { onNavigate("keys") }
            )
        }

        item {
            SwitchCardAction(
                title = "Drivers Mesa Vulkan (Turnip)",
                desc = "Instale drivers gráficos Adreno para corrigir bugs severos nas texturas e triplicar o rendimento.",
                icon = { Icon(Icons.Default.Done, contentDescription = null, tint = TextAccent) },
                accentColor = TextAccent,
                testTag = "action_drivers_manager",
                onClick = { onNavigate("drivers") }
            )
        }

        item {
            SwitchCardAction(
                title = "Biblioteca e Logs (${totalRoms} un.)",
                desc = "Monitore as ROMs otimizadas prontas para uso e exporte estatísticas do sistema.",
                icon = { Icon(Icons.Default.List, contentDescription = null, tint = Color.LightGray) },
                accentColor = Color.LightGray,
                testTag = "action_roms_library",
                onClick = { onNavigate("library") }
            )
        }
    }
}

@Composable
fun ConverterScreen(
    viewModel: SwitchRomViewModel,
    onBack: () -> Unit
) {
    val analysis by viewModel.analysisResult.collectAsState()
    val converting by viewModel.isConverting.collectAsState()
    val progress by viewModel.conversionProgress.collectAsState()
    val conversionLogs by viewModel.conversionLog.collectAsState()

    val context = LocalContext.current
    
    // Set up file picker
    val filePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            val originalName = getFileNameFromUri(context, uri) ?: "SwitchRom.nsz"
            viewModel.analyzeFile(uri, originalName)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("converter_root"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = onBack, modifier = Modifier.testTag("btn_back_converter")) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Voltar", tint = TextPrimary)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(text = "CONVERSOR DE ROMS DE SWITCH", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text(text = "Decomprima e altere o empacotamento para Android", fontSize = 11.sp, color = TextSecondary)
                }
            }
        }

        if (!converting && analysis == null) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, BorderSlate, RoundedCornerShape(16.dp))
                        .testTag("pick_rom_card"),
                    colors = CardDefaults.cardColors(containerColor = DarkGreySurface),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(NeonBlue.copy(alpha = 0.15f), RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, tint = NeonBlue, modifier = Modifier.size(32.dp))
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = "Selecione uma ROM Switch",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Suporta os seguintes formatos comuns de Switch:\nNintendo Submission Package (.nsp)\nCartucho de Switch (.xci)\nNintendo Submission Comprimida (.nsz)",
                            fontSize = 11.sp,
                            color = TextSecondary,
                            textAlign = TextAlign.Center,
                            lineHeight = 16.sp
                        )
                        
                        Spacer(modifier = Modifier.height(20.dp))
                        
                        Button(
                            onClick = { filePicker.launch("*/*") },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonBlue),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("button_select_rom_file")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Procurar Arquivo de ROM", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Analysis Result Card
        analysis?.let { result ->
            if (result.isLoading) {
                item {
                    SwitchLoader("Analisando cabeçalho e estrutura interna...")
                }
            } else {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, BorderSlate, RoundedCornerShape(16.dp))
                            .testTag("analysis_card"),
                        colors = CardDefaults.cardColors(containerColor = DarkGreySurface),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(
                                            if (result.isPfs0Valid) TextAccent.copy(alpha = 0.15f) else WarningYellow.copy(alpha = 0.15f),
                                            RoundedCornerShape(8.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (result.isPfs0Valid) Icons.Default.Check else Icons.Default.Warning,
                                        contentDescription = null,
                                        tint = if (result.isPfs0Valid) TextAccent else WarningYellow
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = result.name,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "${result.originalFormat} Detalhado • ${viewModel.formatSize(result.sizeBytes)}",
                                        fontSize = 12.sp,
                                        color = TextSecondary
                                    )
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider(color = BorderSlate)
                            Spacer(modifier = Modifier.height(12.dp))

                            // Metadata Grid
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Column {
                                    Text(text = "Title ID do Jogo", fontSize = 10.sp, color = TextSecondary)
                                    Text(text = result.titleId, fontSize = 12.sp, color = TextPrimary, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(text = "Required Firmware", fontSize = 10.sp, color = TextSecondary)
                                    Text(text = "v${result.firmwareRequired}", fontSize = 12.sp, color = NeonRed, fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            
                            // Explanation box
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFF1E212A), RoundedCornerShape(8.dp))
                                    .border(1.dp, BorderSlate, RoundedCornerShape(8.dp))
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = result.statusMessage,
                                    fontSize = 11.sp,
                                    color = TextPrimary,
                                    lineHeight = 15.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Action buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { viewModel.analyzeFile(result.uri!!, result.name) },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                                    border = BorderStroke(1.dp, BorderSlate),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Reanalisar", fontSize = 12.sp)
                                }

                                Button(
                                    onClick = { viewModel.transformRom(result) },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = NeonRed),
                                    modifier = Modifier
                                        .weight(1.2f)
                                        .testTag("action_execute_transformation")
                                ) {
                                    Icon(Icons.Default.Refresh, contentDescription = null, tint = Color.White)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Transformar ROM", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Conversion Progress overlay style
        if (converting) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, BorderSlate, RoundedCornerShape(16.dp))
                        .testTag("conversion_progress_panel"),
                    colors = CardDefaults.cardColors(containerColor = DarkGreySurface),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "OTIMIZANDO EM ANDAMENTO...",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonRed,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .testTag("conversion_progress_bar"),
                            color = NeonBlue,
                            trackColor = BorderSlate,
                        )
                        
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "Processando Blocos PFS0", fontSize = 10.sp, color = TextSecondary)
                            Text(text = "${(progress * 100).toInt()}% completo", fontSize = 10.sp, color = TextPrimary, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = BorderSlate)
                        Spacer(modifier = Modifier.height(12.dp))

                        // Live terminal log
                        Text(
                            text = "LOG DO SISTEMA DE CONVERSÃO:",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp)
                                .background(Color.Black, RoundedCornerShape(8.dp))
                                .border(1.dp, BorderSlate, RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            val state = rememberScrollState()
                            LaunchedEffect(conversionLogs.size) {
                                state.animateScrollTo(state.maxValue)
                            }
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(state)
                            ) {
                                conversionLogs.forEach { log ->
                                    Text(
                                        text = log,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 10.sp,
                                        color = if (log.contains("[Sucesso]") || log.contains("🏆")) TextAccent else if (log.contains("[Aviso]")) WarningYellow else Color.LightGray,
                                        modifier = Modifier.padding(vertical = 1.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OptimizerScreen(
    viewModel: SwitchRomViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var selectedGameIndex by remember { mutableStateOf(0) }
    var selectedResolutionIndex by remember { mutableStateOf(1) }
    var userHardwareProfile by remember { mutableStateOf("Alto Desempenho (8GB+ RAM)") }
    var enableNCE by remember { mutableStateOf(true) }
    var enableAsyncShaders by remember { mutableStateOf(true) }
    
    // Preset Game options and configurations
    val gamesList = listOf(
        Pair("The Legend of Zelda: Tears of the Kingdom", "0100F2C0115B6000"),
        Pair("The Legend of Zelda: Breath of the Wild", "01007EF00011E000"),
        Pair("Super Mario Odyssey", "0100000000010000"),
        Pair("Pokémon Scarlet", "0100A3D008C5C000"),
        Pair("Pokémon Violet", "01008F6008C56000"),
        Pair("Mario Kart 8 Deluxe", "0100152000022000"),
        Pair("Metroid Prime Remastered", "0100121014D12000"),
        Pair("Super Smash Bros. Ultimate", "0100AA3007D8C000")
    )
    
    val resolutionOptions = listOf("0.5x (Super Lite - Para celulares de 4GB RAM)", "0.75x (Recomendado - Excelente balanço FPS)", "1.0x (Nativo HD - Para Snapdragon 8 Gen 2+)")
    val resolutionValues = listOf("0.5", "0.75", "1.0")
    
    val generatedIni = remember(selectedGameIndex, selectedResolutionIndex, userHardwareProfile, enableNCE, enableAsyncShaders) {
        viewModel.generateGameConfigIni(
            gameName = gamesList[selectedGameIndex].first,
            targetResolution = resolutionValues[selectedResolutionIndex],
            hardwareClass = userHardwareProfile,
            enableNCE = enableNCE,
            enableAsynchronousShaders = enableAsyncShaders
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("optimizer_root"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = onBack, modifier = Modifier.testTag("btn_back_optimizer")) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Voltar", tint = TextPrimary)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(text = "AJUSTES PARA 60FPS", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text(text = "Gere arquivos de configuração específicos por jogo", fontSize = 11.sp, color = TextSecondary)
                }
            }
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BorderSlate, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = DarkGreySurface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "PASSO 1: SELECIONE O JOGO ALVO",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonBlue
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))

                    // Dropdown simulation for games
                    var showGameDropdown by remember { mutableStateOf(false) }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF1E212A), RoundedCornerShape(8.dp))
                            .border(1.dp, BorderSlate, RoundedCornerShape(8.dp))
                            .clickable { showGameDropdown = !showGameDropdown }
                            .padding(12.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = gamesList[selectedGameIndex].first, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = TextSecondary)
                        }
                        
                        DropdownMenu(
                            expanded = showGameDropdown,
                            onDismissRequest = { showGameDropdown = false },
                            modifier = Modifier.background(DarkGreySurface).border(1.dp, BorderSlate)
                        ) {
                            gamesList.forEachIndexed { index, pair ->
                                DropdownMenuItem(
                                    text = { Text(pair.first, color = TextPrimary, fontSize = 12.sp) },
                                    onClick = {
                                        selectedGameIndex = index
                                        showGameDropdown = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "PASSO 2: RESOLUÇÃO GRÁFICA INTERNA",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonBlue
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Dropdown simulation for resolutions
                    var showResolutionDropdown by remember { mutableStateOf(false) }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF1E212A), RoundedCornerShape(8.dp))
                            .border(1.dp, BorderSlate, RoundedCornerShape(8.dp))
                            .clickable { showResolutionDropdown = !showResolutionDropdown }
                            .padding(12.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = resolutionOptions[selectedResolutionIndex], fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = TextSecondary)
                        }
                        
                        DropdownMenu(
                            expanded = showResolutionDropdown,
                            onDismissRequest = { showResolutionDropdown = false },
                            modifier = Modifier.background(DarkGreySurface).border(1.dp, BorderSlate)
                        ) {
                            resolutionOptions.forEachIndexed { index, text ->
                                DropdownMenuItem(
                                    text = { Text(text, color = TextPrimary, fontSize = 12.sp) },
                                    onClick = {
                                        selectedResolutionIndex = index
                                        showResolutionDropdown = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "PASSO 3: CHASSIS & MOTOR GRÁFICO (NCE)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonBlue
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    // Toggle for NCE
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Ativar Native Code Execution (NCE)", fontSize = 13.sp, color = TextPrimary, fontWeight = FontWeight.Medium)
                            Text("Altamente sugerido. Diminui uso de CPU em 40%", fontSize = 10.sp, color = TextSecondary)
                        }
                        Switch(
                            checked = enableNCE,
                            onCheckedChange = { enableNCE = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = NeonBlue)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Toggle for Async Shaders
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Compilação de Shaders Assíncronos", fontSize = 13.sp, color = TextPrimary, fontWeight = FontWeight.Medium)
                            Text("Inibe trancadas ou 'stuttering' de FPS de textura", fontSize = 10.sp, color = TextSecondary)
                        }
                        Switch(
                            checked = enableAsyncShaders,
                            onCheckedChange = { enableAsyncShaders = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = NeonBlue)
                        )
                    }
                }
            }
        }

        // Output Result INI text
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BorderSlate, RoundedCornerShape(16.dp))
                    .testTag("ini_output_panel"),
                colors = CardDefaults.cardColors(containerColor = DarkGreySurface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ARQUIVO INI GERADO:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonRed
                        )
                        Row {
                            IconButton(onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("switch_config", generatedIni)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Configuração copiada para área de transferência!", Toast.LENGTH_SHORT).show()
                            }, modifier = Modifier.testTag("btn_copy_ini")) {
                                Icon(Icons.Default.Share, contentDescription = "Copiar", tint = TextAccent)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .background(Color.Black, RoundedCornerShape(8.dp))
                            .border(1.dp, BorderSlate, RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = generatedIni,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 9.sp,
                            color = Color.LightGray,
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "COMO INSTALAR NO EMULADOR:\nO Yuzu de Android lê arquivos de configuração particulares por jogo. Para instalar, copie o texto acima, salve o arquivo com o nome '${gamesList[selectedGameIndex].second}.ini' e cole-o na pasta do seu celular:\nAndroid/data/org.yuzu.yuzu_emu/files/config/custom/",
                        color = WarningYellow,
                        fontSize = 10.sp,
                        lineHeight = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun KeysHubScreen(
    viewModel: SwitchRomViewModel,
    onBack: () -> Unit
) {
    val keysStatus by viewModel.keysStatus.collectAsState()
    val context = LocalContext.current

    val keyFilePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            viewModel.importProdKeys(uri)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("keys_hub_root"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = onBack, modifier = Modifier.testTag("btn_back_keys")) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Voltar", tint = TextPrimary)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(text = "CHAVES DE CRIPTOGRAFIA", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text(text = "Gerenciamento e diagnóstico das chaves prod.keys", fontSize = 11.sp, color = TextSecondary)
                }
            }
        }

        // Keys status indicator
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BorderSlate, RoundedCornerShape(16.dp))
                    .testTag("keys_status_card"),
                colors = CardDefaults.cardColors(containerColor = DarkGreySurface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "DIAGNÓSTICO DAS CHAVES",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonBlue
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    when (val status = keysStatus) {
                        is KeysStatus.NoKeys -> {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(NeonRed.copy(alpha = 0.15f), RoundedCornerShape(8.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = null, tint = NeonRed)
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("Chaves Ausentes", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                    Text("Você precisa importar o arquivo prod.keys para jogar.", fontSize = 11.sp, color = TextSecondary)
                                }
                            }
                            Spacer(modifier = Modifier.height(20.dp))
                            Button(
                                onClick = { keyFilePicker.launch("*/*") },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonBlue),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("btn_import_keys")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Importar arquivo prod.keys", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                        is KeysStatus.Valid -> {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(TextAccent.copy(alpha = 0.15f), RoundedCornerShape(8.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = TextAccent)
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("Chaves Instaladas Core!", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                    Text("Status da Decriptação: Ativada e Otimizada", fontSize = 11.sp, color = TextSecondary)
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider(color = BorderSlate)
                            Spacer(modifier = Modifier.height(12.dp))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Column {
                                    Text("Nível do Firmware Estimado", fontSize = 11.sp, color = TextSecondary)
                                    Text(status.firmwareSupport, fontSize = 12.sp, color = TextAccent, fontWeight = FontWeight.Bold)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("Total chaves válidas", fontSize = 11.sp, color = TextSecondary)
                                    Text("${status.linesCount} prod_keys encontradas", fontSize = 12.sp, color = TextPrimary, fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))
                            OutlinedButton(
                                onClick = { viewModel.removeKeys() },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonRed),
                                border = BorderStroke(1.dp, NeonRed.copy(alpha = 0.3f)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = null, tint = NeonRed)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Excluir Chaves Atuais", fontWeight = FontWeight.Bold)
                            }
                        }
                        is KeysStatus.Error -> {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(NeonRed.copy(alpha = 0.15f), RoundedCornerShape(8.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Warning, contentDescription = null, tint = NeonRed)
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("FALHA DE LEITURA", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = NeonRed)
                                    Text(status.message, fontSize = 11.sp, color = TextSecondary)
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { keyFilePicker.launch("*/*") },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonBlue),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Tentar Novamente", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Security guidelines card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BorderSlate, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = DarkGreySurface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "POR QUE MINHAS CHAVES SÃO IMPRESCINDÍVEIS?",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = WarningYellow
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Por razões legais e de patentes de criptografia, os emuladores de Switch Android não possuem as chaves proprietárias de decriptação da Nintendo inclusas por padrão. \n\nO arquivo prod.keys possui todas as hashes de criptografia master para ler o conteúdo interno dos jogos NSP e XCI e carregar no driver Vulkan local sem erros de BIOS de hardware.",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
fun DriverManagerScreen(
    viewModel: SwitchRomViewModel,
    onBack: () -> Unit
) {
    val driversList by viewModel.drivers.collectAsState()
    val specs by viewModel.hardwareSpecs.collectAsState()
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("drivers_root"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = onBack, modifier = Modifier.testTag("btn_back_drivers")) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Voltar", tint = TextPrimary)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(text = "DRIVERS MESA VULKAN (TURNIP)", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text(text = "Oculte glitches de textura e destrave performance máxima", fontSize = 11.sp, color = TextSecondary)
                }
            }
        }

        // Processor advice box
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BorderSlate, RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF13151D)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = WarningYellow, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = if (specs?.isCompatibleWithDrivers == true) 
                            "Seu dispositivo Snapdragon é compatível com os drivers Mesa Turnip listados! Use-os para correções completas." 
                            else "Atenção: Drivers adicionais Mesa Turnip necessitam de GPU Qualcomm Adreno. Como seu processador é Mali, utilize o driver de vídeo padrão nativo do emulador para evitar colisões.",
                        fontSize = 10.sp,
                        color = TextSecondary,
                        lineHeight = 14.sp
                    )
                }
            }
        }

        // Drivers loops
        items(driversList) { driver ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BorderSlate, RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = DarkGreySurface),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = driver.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(text = "Desenvolvedor: ${driver.developer} • ${driver.releaseDate}", fontSize = 10.sp, color = TextSecondary)
                        }
                        
                        Box(
                            modifier = Modifier
                                .background(NeonBlue.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(text = "ZIP: ${driver.sizeMb} MB", fontSize = 10.sp, color = NeonBlue, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = driver.description, fontSize = 11.sp, color = Color.LightGray, lineHeight = 15.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Compatível com: ", fontSize = 10.sp, color = TextSecondary)
                        Text(text = driver.compatibleGpus, fontSize = 10.sp, color = TextAccent, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = BorderSlate)
                    Spacer(modifier = Modifier.height(12.dp))

                    if (driver.isDownloaded) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = TextAccent, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Pronto para Uso (Salvo na Pasta de Downloads)", fontSize = 11.sp, color = TextAccent, fontWeight = FontWeight.Bold)
                            }
                            IconButton(onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("path", driver.localPath)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Caminho do arquivo copiado!", Toast.LENGTH_SHORT).show()
                            }) {
                                Icon(Icons.Default.Share, contentDescription = "Copiar caminho", tint = TextSecondary, modifier = Modifier.size(18.dp))
                            }
                        }
                    } else {
                        Button(
                            onClick = { 
                                viewModel.downloadDriver(driver) 
                                Toast.makeText(context, "${driver.name} baixado perfeitamente!", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonRed),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().height(36.dp)
                        ) {
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Baixar e Salvar Driver", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LibraryScreen(
    viewModel: SwitchRomViewModel,
    onBack: () -> Unit
) {
    val romsList by viewModel.roms.collectAsState()
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("library_root"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = onBack, modifier = Modifier.testTag("btn_back_library")) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Voltar", tint = TextPrimary)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(text = "ROMBIBLIOTECA ANDROID", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text(text = "Gerencie seus jogos Switch adaptados e convertidos", fontSize = 11.sp, color = TextSecondary)
                }
            }
        }

        if (romsList.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp)
                        .border(1.dp, BorderSlate, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = DarkGreySurface),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Nenhuma ROM Catalogada",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Importe e decompressa seus jogos Switch no painel Conversor para listar seus arquivos funcionais aqui.",
                            fontSize = 11.sp,
                            color = TextSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(romsList) { rom ->
                var isExpanded by remember { mutableStateOf(false) }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, BorderSlate, RoundedCornerShape(12.dp))
                        .clickable { isExpanded = !isExpanded },
                    colors = CardDefaults.cardColors(containerColor = DarkGreySurface),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(NeonBlue.copy(alpha = 0.15f), RoundedCornerShape(8.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = NeonBlue)
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = rom.name,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "${rom.targetFormat} • ${viewModel.formatSize(rom.sizeBytes)}",
                                        fontSize = 11.sp,
                                        color = TextAccent
                                    )
                                }
                            }
                            
                            Icon(
                                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = TextSecondary
                            )
                        }

                        if (isExpanded) {
                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = BorderSlate)
                            Spacer(modifier = Modifier.height(12.dp))

                            Text(text = "Title ID:", fontSize = 10.sp, color = TextSecondary)
                            Text(text = rom.titleId, fontSize = 11.sp, color = TextPrimary, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(text = "Compatibilidade de Firmware:", fontSize = 10.sp, color = TextSecondary)
                            Text(text = "Nativo em Firmware v${rom.firmwareRequired}+", fontSize = 11.sp, color = NeonRed, fontWeight = FontWeight.Bold)

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(text = "Emulator de Destino Recomendado:", fontSize = 10.sp, color = TextSecondary)
                            Text(text = rom.emulatorTarget, fontSize = 11.sp, color = TextPrimary, fontWeight = FontWeight.Bold)

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(text = "Notas de Transformação:", fontSize = 10.sp, color = TextSecondary)
                            Text(text = rom.customNotes, fontSize = 11.sp, color = Color.LightGray)

                            Spacer(modifier = Modifier.height(12.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        val clip = ClipData.newPlainText("title", rom.titleId)
                                        clipboard.setPrimaryClip(clip)
                                        Toast.makeText(context, "TitleID copiado!", Toast.LENGTH_SHORT).show()
                                    },
                                    shape = RoundedCornerShape(6.dp),
                                    border = BorderStroke(1.dp, BorderSlate),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Copiar ID", fontSize = 10.sp, color = TextPrimary)
                                }

                                Button(
                                    onClick = { 
                                        viewModel.deleteRom(rom)
                                        Toast.makeText(context, "Log excluído com sucesso", Toast.LENGTH_SHORT).show()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = NeonRed.copy(alpha = 0.8f)),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Excluir Log", fontSize = 10.sp, color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// Simple helper to fetch filenames from Storage Access Framework URIs safely
private fun getFileNameFromUri(context: Context, uri: android.net.Uri): String? {
    var result: String? = null
    if (uri.scheme == "content") {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        try {
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                if (index != -1) {
                    result = cursor.getString(index)
                }
            }
        } finally {
            cursor?.close()
        }
    }
    if (result == null) {
        result = uri.path
        val cut = result?.lastIndexOf('/') ?: -1
        if (cut != -1) {
            result = result?.substring(cut + 1)
        }
    }
    return result
}
