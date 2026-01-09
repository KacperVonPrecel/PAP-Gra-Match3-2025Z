import { CharacterType } from '../../user-data/user-data-service';

export interface UserCharacterData {
	readonly characterType: CharacterType;
	readonly unlockedData?: UserCharacterUnlockedData;
}

export interface UserCharacterUnlockedData {
	readonly level: number;
	//XXX handle level max character
	readonly requiredCopiesForNextLevel: number;
	readonly currentCopiesCount: number;
}
