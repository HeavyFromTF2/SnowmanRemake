package pt.ipbeja.estig.po2.snowman.model;

/**
 * Martim Dias - 24290
 * Representa os diferentes estados de uma bola de neve no jogo.
 *
 * Uma bola de neve pode variar de tamanho (SMALL, MEDIUM, LARGE) ou representar combinações
 * intermediárias enquanto está a ser empilhada para formar o boneco de neve completo
 */
public enum SnowballStatus {
    SMALL,
    MEDIUM,
    LARGE,
    MEDIUM_SMALL,
    LARGE_SMALL,
    LARGE_MEDIUM,
    FULL_SNOWMAN
}
