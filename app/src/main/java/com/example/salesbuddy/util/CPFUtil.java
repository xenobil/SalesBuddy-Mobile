package com.example.salesbuddy.util;


    public class CPFUtil {

        public static String gerarCPFCompleto(String cpfParcial) {
            if (cpfParcial == null || cpfParcial.length() != 9) {
                throw new IllegalArgumentException("CPF parcial deve ter 9 dígitos.");
            }

            int primeiroDigito = calcularDigitoVerificador(cpfParcial, 10);
            int segundoDigito = calcularDigitoVerificador(cpfParcial + primeiroDigito, 11);

            return cpfParcial + primeiroDigito + segundoDigito;
        }

        private static int calcularDigitoVerificador(String cpf, int pesoInicial) {
            int soma = 0;
            for (int i = 0; i < cpf.length(); i++) {
                int digito = Character.getNumericValue(cpf.charAt(i));
                soma += digito * pesoInicial;
                pesoInicial--;
            }

            int resto = (soma * 10) % 11;
            if (resto == 10 || resto == 11) {
                resto = 0;
            }

            return resto;
        }

        public static void main(String[] args) {
            String cpfParcial = "123456789";
            String cpfCompleto = gerarCPFCompleto(cpfParcial);
            System.out.println("CPF completo: " + cpfCompleto);
        }
    }


