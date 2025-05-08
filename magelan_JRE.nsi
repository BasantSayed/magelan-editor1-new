;; Magelan 1.0 RC4 Installer
;; 01/2006, Assen Antov

;--------------------------------
;Include Modern UI

  !include "MUI.nsh"

;--------------------------------
;General

  ;Name and file
  Name "Magelan 1.0 RC4"
  OutFile "magelan-1.0_4-win-JRE.exe"

  ;Default installation folder
  InstallDir "$PROGRAMFILES\magelan-1.0-4"
  
  ;Get installation folder from registry if available
  InstallDirRegKey HKCU "Software\magelan-1.0-RC4" ""

;--------------------------------
;Interface Configuration

;  !define MUI_ICON "magelan.ico"
;  !define MUI_UNICON "${NSISDIR}\Contrib\Graphics\Icons\modern-uninstall.ico"
  !define MUI_HEADERIMAGE
  !define MUI_HEADERIMAGE_BITMAP_NOSTRETCH
  !define MUI_HEADERIMAGE_RIGHT
;  !define MUI_HEADERIMAGE_BITMAP "magelan.bmp"
  !define MUI_HEADERIMAGE_BITMAP "installer.bmp"
  ;!define MUI_ABORTWARNING

;--------------------------------
;Pages

  !insertmacro MUI_PAGE_LICENSE "doc\license.txt"
  !insertmacro MUI_PAGE_COMPONENTS
  !insertmacro MUI_PAGE_DIRECTORY
  !insertmacro MUI_PAGE_INSTFILES
  
  !insertmacro MUI_UNPAGE_CONFIRM
  !insertmacro MUI_UNPAGE_INSTFILES
  
;--------------------------------
;Languages
 
  !insertmacro MUI_LANGUAGE "English"

;--------------------------------
;Installer Sections

Section "Program Files" SecEditor

  SectionIn RO
  SetOutPath "$INSTDIR"

  File /r /x jre-1_5_0_06-windows-i586-p.exe /x src *.*
  
  ;Store installation folder
  WriteRegStr HKCU "Software\magelan-1.0-RC4" "" $INSTDIR

  ; desctop icon
  CreateShortCut "$DESKTOP\Magelan 1.0 RC4.lnk" "javaw.exe" \
  '-jar "$INSTDIR\magelan-editor.jar"' "$INSTDIR\magelan.ico"
  
  ; start menu 
  CreateDirectory "$SMPROGRAMS\Magelan 1.0 RC4"
  CreateShortCut "$SMPROGRAMS\Magelan 1.0 RC4\Magelan.lnk" "javaw.exe" \
    '-Xmx512M -Xss16M -jar "$INSTDIR\magelan-editor.jar"' "$INSTDIR\magelan.ico"
  CreateShortCut "$SMPROGRAMS\Magelan 1.0 RC4\Read Me.lnk" "$INSTDIR\doc\readme.html" ""
  CreateShortCut "$SMPROGRAMS\Magelan 1.0 RC4\License.lnk" "$INSTDIR\doc\license.txt" ""
  CreateShortCut "$SMPROGRAMS\Magelan 1.0 RC4\Uninstall.lnk" "$INSTDIR\Uninstall.exe" ; "" "$INSTDIR\Uninstall.exe" 1

  ;Create uninstaller
  WriteUninstaller "$INSTDIR\Uninstall.exe"

SectionEnd


Section "Sources" SecSrc

  SetOutPath "$INSTDIR\src"
  
  File /r src\*.*

SectionEnd



Section "JRE 1.5.0" SecJRE

  SetOutPath "$INSTDIR"
  
  File jre-1_5_0_06-windows-i586-p.exe
  ExecWait $INSTDIR\jre-1_5_0_06-windows-i586-p.exe
  Delete $INSTDIR\jre-1_5_0_06-windows-i586-p.exe

SectionEnd


;--------------------------------
;Descriptions

  ;Language strings
  LangString DESC_SecEditor ${LANG_ENGLISH} "Editor program files."
  LangString DESC_SecSrc ${LANG_ENGLISH} "Program source files (for developers)."
  LangString DESC_SecJRE ${LANG_ENGLISH} "Java Runtime Environment 1.5.0-6 (needed to run the software)."

  ;Assign language strings to sections
  !insertmacro MUI_FUNCTION_DESCRIPTION_BEGIN
    !insertmacro MUI_DESCRIPTION_TEXT ${SecEditor} $(DESC_SecEditor)
    !insertmacro MUI_DESCRIPTION_TEXT ${SecSrc} $(DESC_SecSrc)
    !insertmacro MUI_DESCRIPTION_TEXT ${SecJRE} $(DESC_SecJRE)
  !insertmacro MUI_FUNCTION_DESCRIPTION_END
 
;--------------------------------
;Uninstaller Section

Section "Uninstall"

  ;; delete files
  RMDir /r "$INSTDIR"

  ;; delete start menu items
  RMDir /r "$SMPROGRAMS\Magelan 1.0 RC4"

  ;; delete desktop shortcut
  Delete "$DESKTOP\Magelan 1.0 RC4.lnk"

  DeleteRegKey /ifempty HKCU "Software\magelan-1.0-RC4"

SectionEnd