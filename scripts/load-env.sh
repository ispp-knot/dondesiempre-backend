#!/bin/bash
# Script para cargar variables de entorno desde .env en Linux/Mac
# Uso: source load-env.sh  (o . load-env.sh)

ENV_FILE="${1:-.env}"

# Colores para output
GREEN='\033[0;32m'
CYAN='\033[0;36m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Verificar si el archivo .env existe
if [ ! -f "$ENV_FILE" ]; then
    echo -e "${RED}Error: No se encontró el archivo $ENV_FILE${NC}"
    return 1 2>/dev/null || exit 1
fi

echo -e "${GREEN}Cargando variables de entorno desde $ENV_FILE...${NC}"
echo

# Leer el archivo línea por línea
while IFS= read -r line || [ -n "$line" ]; do
    # Eliminar espacios en blanco al inicio y final
    line=$(echo "$line" | sed -e 's/^[[:space:]]*//' -e 's/[[:space:]]*$//')
    
    # Ignorar líneas vacías y comentarios
    if [ -z "$line" ] || [[ "$line" == \#* ]]; then
        continue
    fi
    
    # Verificar que la línea tenga el formato KEY=VALUE
    if [[ "$line" =~ ^[a-zA-Z_][a-zA-Z0-9_]*= ]]; then
        # Extraer key y value
        key=$(echo "$line" | cut -d '=' -f 1)
        value=$(echo "$line" | cut -d '=' -f 2-)
        
        # Remover comillas si existen
        value=$(echo "$value" | sed -e 's/^"//' -e 's/"$//' -e "s/^'//" -e "s/'$//")
        
        # Exportar la variable
        export "$key=$value"
        echo -e "  ${CYAN}✓ $key${NC}"
    fi
done < "$ENV_FILE"

echo
echo -e "${GREEN}✓ Variables de entorno cargadas exitosamente${NC}"
echo -e "${YELLOW}Nota: Estas variables solo están disponibles en la sesión actual de terminal${NC}"
