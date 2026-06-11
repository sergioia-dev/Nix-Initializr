{
  description = "A Development Environment for Nix Initializr";

  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-26.05";
    flake-utils.url = "github:numtide/flake-utils";
  };

  outputs =
    {
      self,
      nixpkgs,
      flake-utils,
    }:
    flake-utils.lib.eachDefaultSystem (
      system:
      let
        pkgs = import nixpkgs {
          inherit system;
          config.allowUnfree = true;
        };
      in
      {
        devShells = {
          frontend = pkgs.mkShell {
            buildInputs = with pkgs; [
              nodejs
            ];

            shellHook = ''
              if [ -f .env ]; then
                 set -a
                 source .env
                 set +a
              fi
               echo "Frontend Environment Initialized";
            '';
          };

          backend = pkgs.mkShell {
            buildInputs = with pkgs; [
              postgresql
              redis
            ];

            JAVA_HOME = "${pkgs.openjdk25.home}";

            shellHook = ''
              if [ -f .env ]; then
                 set -a
                 source .env
                 set +a
              fi
              nix develop github:sergioia-dev/nix-environments#java25-maven
              exit
            '';
          };

          podman = pkgs.mkShell { shellHook = "nix develop github:sergioia-dev/nix-environments#podman"; };
        };

        apps = {

          backend = {
            type = "app";
            program =
              let
                script = pkgs.writeShellScriptBin "run-spring" ''
                  if [[ "$(basename "$PWD")" == "backend" ]]; then
                    if [ -f .env ]; then
                      set -a
                      source .env
                      set +a
                    fi
                    export JAVA_HOME="${pkgs.openjdk25.home}"
                    ./mvnw spring-boot:run
                    cd ..;
                  fi
                '';
              in
              "${script}/bin/run-spring";
          };
        };
      }
    );
}
