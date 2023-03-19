class LRUCache<T> {
	private readonly capacity: number;
	private queue: string[];
	private readonly _cache: Map<string, T>;
	
	constructor(capacity: number) {
		this.capacity = capacity;
		this._cache = new Map();
		this.queue = [];
	}
	
	public get cache(): Map<string, T> {
		return this._cache;
	}
	
	get(key: string): T | undefined {
		const value = this.cache.get(key);
		if (value === undefined) {
			return undefined;
		}
		this.queue.splice(this.queue.indexOf(key), 1);
		this.queue.push(key);
		return value;
	}
	
	put(key: string, value: T): void {
		if (this._cache.has(key)) {
			this.queue.splice(this.queue.indexOf(key), 1);
		} else if (this._cache.size >= this.capacity) {
			const oldestKey = this.queue.shift();
			if (oldestKey !== undefined) {
				this._cache.delete(oldestKey);
			}
		}
		this._cache.set(key, value);
		this.queue.push(key);
	}
}

class SessionStorageLRUCache<T> {
	private cache: LRUCache<T>;
	
	constructor(capacity: number) {
		this.cache = new LRUCache(capacity);
	}
	
	get(key: string): T | undefined {
		const value = this.cache.get(key);
		if (value === undefined) {
			return undefined;
		}
		return value;
	}
	
	put(key: string, value: T): void {
		this.cache.put(key, value);
	}
	
}

export default new SessionStorageLRUCache<string>(20);