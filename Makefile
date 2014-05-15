BIN = ./bin/
LIB = ./lib/
SRC = ./src/

EMPTY =
SPACE = $(EMPTY) $(EMPTY)

LIBRARIES = $(wildcard $(LIB)*.jar)
PACKAGES = cits3002/client/ cits3002/common/ cits3002/common/handlers/ cits3002/common/messages/ cits3002/server/ cits3002/util/
JAVA_FILES = $(subst $(SRC),$(EMPTY),$(foreach DIR,$(PACKAGES),$(wildcard $(SRC)$(DIR)*.java)))
CLASS_FILES = $(JAVA_FILES:.java=.class)

JAVAC = javac
JAVAFLAGS = -g -d $(BIN) -cp $(SRC):$(subst $(SPACE),:,$(LIBRARIES))

all: $(addprefix $(BIN),$(CLASS_FILES))

$(BIN)%.class: $(SRC)%.java
	@test -d $(BIN) || mkdir $(BIN)
	$(JAVAC) $(JAVAFLAGS) $<

clean:
	rm -rf $(BIN)*
