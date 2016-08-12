!include MUI2.nsh

!define VERSION "0.4.8"
!define JAVA_BIN "java"

Name "Andor's Trail Content Studio v${VERSION}"
OutFile "ATCS_v${VERSION}_Setup.exe"
InstallDir "$PROGRAMFILES\ATCS\"

;SetCompressor /SOLID /FINAL lzma

Var StartMenuFolder

!define MUI_WELCOMEPAGE_TITLE "Welcome to Andor's Trail Content Studio installer"
!define MUI_WELCOMEPAGE_TEXT "This will install Andor's Trail Content Studio v${VERSION} installer"
!define MUI_FINISHPAGE_TEXT "Andor's Trail Content Studio v${VERSION} install completed !"
!define MUI_STARTMENUPAGE_DEFAULTFOLDER "Andor's Trail Content Studio"
!define MUI_PAGE_HEADER_TEXT "Installing Andor's Trail Content Studio v${VERSION}"


;Start Menu Folder Page Configuration
!define MUI_STARTMENUPAGE_REGISTRY_ROOT "HKCU" 
!define MUI_STARTMENUPAGE_REGISTRY_KEY "Software\ATCS" 
!define MUI_STARTMENUPAGE_REGISTRY_VALUENAME "ATCS"

!define MUI_HEADERIMAGE
;!define MUI_HEADER_TRANSPARENT_TEXT
!define MUI_HEADERIMAGE_BITMAP nsisHeader.bmp
!define MUI_HEADERIMAGE_BITMAP_NOSTRETCH
;!define MUI_HEADERIMAGE_RIGHT
;!define MUI_HEADERIMAGE_BITMAP_STRETCH "AspectFitHeight"
!define MUI_HEADERIMAGE_UNBITMAP nsisHeader.bmp
;!define MUI_HEADERIMAGE_UNBITMAP_STRETCH "AspectFitHeight"
!define MUI_WELCOMEFINISHPAGE_BITMAP nsisBorderBanner.bmp
!define MUI_UNWELCOMEFINISHPAGE_BITMAP nsisBorderBanner.bmp
;!define MUI_BGCOLOR "E3E3E3"
!define MUI_ABORTWARNING

!insertmacro MUI_PAGE_WELCOME
!insertmacro MUI_PAGE_DIRECTORY
!insertmacro MUI_PAGE_STARTMENU "ATCS" $StartMenuFolder
!insertmacro MUI_PAGE_INSTFILES
!insertmacro MUI_PAGE_FINISH

!insertmacro MUI_UNPAGE_CONFIRM
!insertmacro MUI_UNPAGE_INSTFILES


!insertmacro MUI_LANGUAGE "English"

Section install


  SetOutPath $INSTDIR
  file "ATCS.ico"
  
  Call GetJRE
  Pop $R0
  FileOpen $9 "ATCS.cmd" w
  FileWrite $9 'start "" "$R0" -Xmx512M -cp "lib\AndorsTrainer_v0.1.1.jar;lib\jide-oss.jar;lib\ui.jar;lib\junit-4.10.jar;lib\json_simple-1.1.jar;lib\rsyntaxtextarea.jar;lib\prefuse.jar;lib\bsh-2.0b4.jar;lib\ATCS_v${VERSION}.jar" com.gpl.rpg.atcontentstudio.ATContentStudio'
  FileClose $9
  
  SetOutPath "$INSTDIR\lib\"
  file "jide-oss.jar"
  file "ui.jar"
  file "AndorsTrainer_v0.1.2.jar"
  file "junit-4.10.jar"
  file "json_simple-1.1.jar"
  file "ATCS_v${VERSION}.jar"
  file "rsyntaxtextarea.jar"
  file "prefuse.jar"
  file "bsh-2.0b4.jar"

  SetOutPath $INSTDIR
  
  WriteUninstaller "$INSTDIR\Uninstall.exe"
  
  
  !insertmacro MUI_STARTMENU_WRITE_BEGIN "ATCS"
    
    ;Create shortcuts
    CreateDirectory "$SMPROGRAMS\$StartMenuFolder"
    CreateShortcut "$SMPROGRAMS\$StartMenuFolder\Andor's Trail Content Studio.lnk" "$INSTDIR\ATCS.cmd" "" "$INSTDIR\ATCS.ico"
    CreateShortcut "$SMPROGRAMS\$StartMenuFolder\Uninstall.lnk" "$INSTDIR\Uninstall.exe"
  
  !insertmacro MUI_STARTMENU_WRITE_END
  
SectionEnd

Section uninstall
  
  Delete "$INSTDIR\lib\jide-oss.jar"
  Delete "$INSTDIR\lib\ui.jar"
  Delete "$INSTDIR\lib\junit-4.10.jar"
  Delete "$INSTDIR\lib\json_simple-1.1.jar"
  Delete "$INSTDIR\lib\AndorsTrainer_v0.1.2.jar"
  Delete "$INSTDIR\lib\ATCS_v${VERSION}.jar"
  Delete "$INSTDIR\lib\rsyntaxtextarea.jar"
  Delete "$INSTDIR\lib\prefuse.jar"
  RMDir "$INSTDIR\lib\"
  Delete "$INSTDIR\ATCS.ico"
  Delete "$INSTDIR\ATCS.cmd"
  
  Delete "$INSTDIR\Uninstall.exe"

  RMDir "$INSTDIR"
  
  !insertmacro MUI_STARTMENU_GETFOLDER "ATCS" $StartMenuFolder
    
  Delete "$SMPROGRAMS\$StartMenuFolder\Uninstall.lnk"
  Delete "$SMPROGRAMS\$StartMenuFolder\Andor's Trail Content Studio.lnk"
  RMDir "$SMPROGRAMS\$StartMenuFolder"
  
SectionEnd


Function GetJRE
;
;  Find JRE (javaw.exe)
;  DISABLED 1 - in .\jre directory (JRE Installed with application)
;  2 - in JAVA_HOME environment variable
;  3 - in the registry
;  4 - assume javaw.exe in current dir or PATH
 
  Push $R0
  Push $R1
 
  ;ClearErrors
  ;StrCpy $R0 "$EXEDIR\jre\bin\javaw.exe"
  ;IfFileExists $R0 JreFound
  ;StrCpy $R0 ""
 
  ClearErrors
  ReadEnvStr $R0 "JAVA_HOME"
  StrCpy $R0 "$R0\bin\${JAVA_BIN}.exe"
  IfErrors 0 JreFound
 
  ClearErrors
  ReadRegStr $R1 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment" "CurrentVersion"
  ReadRegStr $R0 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment\$R1" "JavaHome"
  StrCpy $R0 "$R0\bin\${JAVA_BIN}.exe"
 
  IfErrors 0 JreFound
  StrCpy $R0 "${JAVA_BIN}.exe"
 
 JreFound:
  Pop $R1
  Exch $R0
FunctionEnd
